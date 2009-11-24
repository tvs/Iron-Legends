/**
 * Overrides stock ResourceFactory so we can have transparent PNG loading
 */
package jig.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.internal.TerseLogFormatter;
import jig.engine.util.Vector2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;


/**
 * An abstract factory that describes the interface required to create Resources
 * for a particular graphics backend.
 * 
 * Each graphics backend has its own concrete subclass of ResourceFactory.
 * 
 * The ResourceFactory and its subclasses are designed to allow
 * memory efficient sharing of resources such as images or audio clips
 * across multiple object classes and instances. Methods are provided
 * for pre-loading and retrieving resources from an internal cache,
 * and these methods attempt to <em>fail gracefully</em> if the 
 * requested resource cannot be found.  By this, we mean that 
 * place-holder resources will be supplied whenever possible so as to
 * facilitate rapid prototyping and testing of ideas.
 * 
 * Informational and warning messages are sent to the package-level logging
 * object. The following policy is used to determine the informational level of
 * a message:
 * <ul>
 * <li> If the situation should be expected to result in a downstream error
 * (e.g., because a requested resource can't be found or loaded) the message
 * should have a WARNING level</li>
 * <li> If a suspicious situation is internally resolved by the ResourceFactory,
 * but may have undesired consequences (e.g., because an the image is found in
 * an unexpected place on the search path, an ersatz image is substituted for
 * the real one, or an image resource is reloaded), the message should have an
 * INFO level</li>
 * <li> If the event gives details about an INFO level message, the level should
 * be FINE or FINER</li>
 * 
 * </ul>
 * 
 * The <code>Logger</code> returned by <code>getJIGLogger</code> is either
 * initialized by a user-specified properties file, class, or by the default
 * scheme which creates directs logging output to stderr, sets the
 * <code>Logger</code> level to <code>INFO</code> (according to Java docs,
 * the ConsoleHandler should have a level of <code>ALL</code>).
 *  
 * @author Scott Wallace
 * 
 * @see java.util.logging.Handler
 * 
 */
public abstract class ResourceFactory {

	protected static Logger gameLog;
	protected static Logger jigLog;
	
	private static int clipsRequested = 0;
	
	private static final int NCLIPS = 2;

	/** A pattern indicating the # of rows, columns and frames in the image. */
	private static final Pattern RCF_FRAME_FILE_PATTERN = Pattern
			.compile(".*-(\\d+)-(\\d+)-(\\d+)\\....");

	/** A cache of frameset ImageResources. */
	protected HashMap<String, List<ImageResource>> imgRscCache;

	/** A cache of AudioClip objects. */
	protected HashMap<String, AudioClip> audioClipCache;

	/** A cache of BitmapFont objects. */
	protected HashMap<String, BitmapFont> fontCache;

	private static ResourceFactory theResourceFactory;

	/** Initializes the Log object. */
	static {
		gameLog = Logger.getLogger(ResourceFactory.class.getPackage().getName()+"game");
		jigLog = Logger.getLogger(ResourceFactory.class.getPackage().getName());
		
		// Also, set the root logger to a terse format, unless
		// the user has specified something else...
		boolean userConfig = false;

		userConfig |= 
			(System.getProperty("java.util.logging.config.file") != null);

		userConfig |= 
			(System.getProperty("java.util.logging.config.class") != null);

		if (!userConfig) {
			gameLog.setLevel(Level.INFO);
			if (gameLog.getHandlers().length == 0) {
				gameLog.setUseParentHandlers(false);
				ConsoleHandler h = new ConsoleHandler();
				h.setFormatter(new TerseLogFormatter());
				gameLog.addHandler(h);
			}
			jigLog.setLevel(Level.INFO);
			if (jigLog.getHandlers().length == 0) {
				jigLog.setUseParentHandlers(false);
				ConsoleHandler h = new ConsoleHandler();
				h.setFormatter(new TerseLogFormatter());
				jigLog.addHandler(h);
			}

		}

	}

	/**
	 * Gets the currently selected Resource Factory.
	 * 
	 * @return the current resource factory
	 * 
	 * @see jig.engine.j2d.J2DResourceFactory#makeCurrentResourceFactory()
	 * @see jig.engine.lwjgl.LWResourceFactory#makeCurrentResourceFactory()
	 */
	public static ResourceFactory getFactory() {
		return theResourceFactory;
	}

	/**
	 * @return the JIG Engine logger for internal (engine) events.
	 */
	public static Logger getJIGLogger() {
		return jigLog;
	}

	/**
	 * Sets the current, canonical, resource factory. Once set, the factory
	 * cannot be changed for the duration of the application.
	 * 
	 * @param f
	 *            a concrete resource factory instance
	 */
	protected static void setCurrentResourceFactory(final ResourceFactory f) {
		if (theResourceFactory == null) {
			theResourceFactory = f;

			return;
		}
		throw new IllegalArgumentException(
				"ResourceFactory cannot be modified.");
		}

	/**
	 * Creates a ResourceFactory. Classes that extend ResourceFactory should
	 * have private constructors and return a reference to a singleton instance
	 * through a <code>getResourceFactory</code> method.
	 * 
	 * @see jig.engine.j2d.J2DResourceFactory#getResourceFactory()
	 * @see jig.engine.lwjgl.LWResourceFactory#getResourceFactory()
	 * 
	 */
	protected ResourceFactory() {
		initializeResources();
	}

	/**
	 * Initializes all resource caches. Can be used to free all resources. 
	 */
	private final void initializeResources() {
		imgRscCache = new HashMap<String, List<ImageResource>>(40);
		audioClipCache = new HashMap<String, AudioClip>(20);
		fontCache = new HashMap<String, BitmapFont>(20);
	}

	/**
	 * Creates a new ImageResource using the internal formating requirements of
	 * the concrete ResourceFactory instance. This method must be overridden by
	 * any subclass of ResourceFactory.
	 * 
	 * @param originalImg
	 *            the original image
	 * @param transparency
	 *            the desired transparency mode
	 * @param w
	 *            the width of the resulting image resource
	 * @param h
	 *            the height of the resulting image resource
	 * @param xoffset
	 *            the xoffset of the resulting image with respect to the
	 *            original image
	 * @param yoffset
	 *            the yoffset of the resulting image with respect to the
	 *            original image
	 * @return a new 'internally formatted' image resource
	 * 
	 */
	protected abstract ImageResource createImageResource(
			BufferedImage originalImg,
			int transparency, int w, int h, int xoffset, int yoffset);
	
	/**
	 * Loads resources specified in an xml file.
	 * 
	 * @param xmlPath the path to the xml resource file
	 * @param xmlName the name of the xml resource file
	 * @return <code>true</code> if no unrecoverable errors occurred.
	 */
	public boolean loadResources(final String xmlPath, final String xmlName) {
		XMLBasedLoader loader = new XMLBasedLoader();
		return loader.loadResourceSheet(xmlPath, xmlName);
	}
	
	/**
	 * Frees the resources specified in an xml file.
	 * 
	 * @param xmlPath the path to the xml resource file
	 * @param xmlName the name of the xml resource file
	 * @return <code>true</code> if no unrecoverable errors occurred.
	 */
	public boolean freeResources(final String xmlPath, final String xmlName) {
		XMLBasedLoader loader = new XMLBasedLoader();
		return loader.unloadResourceSheet(xmlPath, xmlName);
	}

	/**
	 * Free all resources currently held by the resource factory.
	 */
	public void freeAllResources() {
		initializeResources();
	}
	
	public boolean areFramesLoaded(final String rscName) {
		return imgRscCache.get(rscName) == null ? false: true;
	}
	/**
	 * Gets a frameset from a specified resource name that has already 
	 * been loaded.
	 * 
	 * Note you must first load the resource using a spritesheet, resource
	 * file, PaintableCanvas or putFrames call. 
	 * 
	 * @param rscName
	 *            a stringified URL or internal name
	 * @return the frames in the frameset or null of loading failed.
	 * 
	 */
	public List<ImageResource> getFrames(final String rscName) {

		List<ImageResource> cacheHit = imgRscCache.get(rscName);

		if (cacheHit != null)
			return cacheHit;
		else {
			getJIGLogger().info("Frames " + rscName + " are being dynamically loaded.");
			URL rscURL = findResource(rscName);
			if (!loadFrameResource(rscURL, rscName)) return null;
			
			return imgRscCache.get(rscName);
		}

	}
	
	/**
	 * Makes a cursor using an image animation frame set.
	 * 
	 * @param rscName the name of the image resource
	 * @param hotspot the location of the 'hot spot' or active spot on the image
	 * @param delay sets the time delay between frames of an animated cursor
	 * @return a new cursor resource
	 */
	public abstract CursorResource makeCursor(final String rscName, 
			Vector2D hotspot, long delay);


	/**
	 * Gets a FontResource from the specified Java Font.
	 * 
	 * The current resource factory will attempt to provide the
	 * best font resource reputation of this Java Font that is possible.
 	 * 
	 * @param f the system font to be turned into a FontResource
	 * @param fontColor the color the font should be rendered with
	 * @param backgroundColor the color that should be rendered in the
	 *     background or null if the background should be transparent
	 * @return a font resource usable by the appropriate graphics backend.
	 */
	public abstract FontResource getFontResource(final Font f, Color fontColor, Color backgroundColor);

	/**
	 * Gets a FontResource from the specified Java Font.
	 * 
	 * The current resource factory will attempt to provide the
	 * best font resource for Java Font making the specified
	 * tradeoff for either speed or quality.
 	 * 
	 * @param f the system font to be turned into a FontResource
	 * @param fontColor the color the font should be rendered with
	 * @param backgroundColor the color that should be rendered in the
	 *     background or null if the background should be transparent
	 * @param prioritizeSpeed <code>true</code> if a faster font should be 
	 *     preferred to a potentially slower, but potentially higher quality font.
	 * @return a font resource usable by the appropriate graphics backend.
	 */
	public abstract FontResource getFontResource(final Font f, Color fontColor, Color backgroundColor, boolean prioritizeSpeed);

	/**
	 * Gets a FontResource from the cache.
	 * 
	 * TODO: Maybe add support for loading system fonts from a resource name
	 * 
	 * @param rscName
	 *            the file/resource name: this must be a bitmap image such as
	 *            a png file, not a truetype or other vector font
	 *            
	 * @param transparentBackground
	 *            <code>true</code> if the font should be loaded so as to have
	 *            a transparent background.
	 * 
	 * @return a FontResource if it was already loaded or null if it wasn't
	 * 
	 * @see #getFontResource(Font)
	 * 
	 */
	public FontResource getFontResource(final String rscName,
			final boolean transparentBackground) {
		BitmapFont cached;
		String fontKey = BitmapFont.fontKey(rscName, transparentBackground);

		cached = fontCache.get(fontKey);
		if (cached != null) {
			return cached;
		}
		else
			return null;
	}
	
	/**
	 * Creates a new <code>BitmapFont</code> from the specified file/resource
	 * and stores it in the font cache.
	 * 
	 * Note the resource name is a actual image with the bitmap font data. If you
	 * instead want to create a bitmap font from a Font object you should use the
	 * buildBitmapFont method.
	 * 
	 * TODO: This needs to be private and only loaded through a resource xml file
	 * 
	 * Sends a informational message to the
	 * package logger if a resource with the same name has already been loaded.
	 * 
	 * @param rscName
	 *            the file/resource name
	 * @param transparentBackground
	 *            <code>true</code> if the font should be loaded so as to have
	 *            a transparent background.
	 */
	public void loadFontResource(final String rscName,
			final boolean transparentBackground) {

		String fontKey = BitmapFont.fontKey(rscName, transparentBackground);

		if (fontCache.get(fontKey) != null) {
			gameLog.info("Resource '" + rscName + "' already loaded.");
			return;
		}
		BitmapFont f = BitmapFont.loadFontWriterFont(rscName,
				transparentBackground);
		
		if (f != null) {
			fontCache.put(fontKey, f);
		}
		
	}
	
	/**
	 * Creates a BitmapFont from a system font.
	 * 
	 * TODO: This is not caching the BitmapFont...
	 * 
	 * @param systemFont
	 * @param foreground
	 * @param background
	 * @return
	 */
	protected BitmapFont getBitmapFont(final Font systemFont, 
			final Color foreground, final Color background)
	{
		//rscName = systemFont.toString() + foreground + background
		if(background == null)
			return BitmapFont.buildBitmapFont(systemFont, foreground, new Color(0, 0, 0, 0));
		else
			return BitmapFont.buildBitmapFont(systemFont, foreground, background);
	}
	



	/**
	 * Gets an AudioClip from the cache, loading it if necessary. Typically, it
	 * is a good idea to preload AudioClips at a point where latency is not an
	 * issue (such as before the game begins or between levels).  If the 
	 * audio clip was not preloaded, a warning is issued to the log.
	 * 
	 * If the specified resource cannot be found, or loading fails, a
	 * stand in audio clip is used as a fallback.
	 * 
	 * @param rscName
	 *            the file/resource name
	 * @return an AudioClip
	 */
	public AudioClip getAudioClip(final String rscName) {
		AudioClip cached;

		cached = audioClipCache.get(rscName);
		if (cached != null) {
			return cached;
		}
		gameLog.warning("Resource '" + rscName + "' was not preloaded.");

		cached = AudioClip.createAudioClip(rscName);
		if (cached == null) {
			gameLog.info("Creating stand-in audio clip for " + rscName);
			cached = AudioClip.createAudioClip(nextErsatzClipRsc());
		}
		audioClipCache.put(rscName, cached);
		
		return cached;
	}

	/**
	 * Creates a new <code>AudioClip</code> from the specified file/resource
	 * and stores it in the audio cache. Sends a informational message to the
	 * package logger if a resource with the same name has already been loaded.
	 * 
	 * @param rscName
	 *            the name of the file storing the resource
	 * 
	 * @see jig.engine.audio.AudioClip
	 */
	private void loadAudioClip(final String rscName) {
		if (audioClipCache.get(rscName) != null) {
			gameLog.info("Resource '" + rscName + "' already loaded.");
			return;
		}
		AudioClip c = AudioClip.createAudioClip(rscName);
		if (c == null) {
			gameLog.info("Creating stand-in audio clip for " + rscName);
			c = AudioClip.createAudioClip(nextErsatzClipRsc());
		}
		audioClipCache.put(rscName, c);

	}


	/**
	 * Stores programmatically created images in the resource cache as singleton
	 * instances formatted appropriately for the concrete ResourceFactory
	 * instance. If a resource with the same name already exists, this method
	 * will send a warning to the package logger.
	 * 
	 * @param name
	 *            the name used to retrieve this frameset
	 * @param r
	 *            the original array of images in the frameset
	 */
	public void putFrames(final String name, final BufferedImage[] r) {
		ImageResource[] bframes;

		if (imgRscCache.get(name) != null) {
			gameLog.info("Resource '" + name + "' already exists.");
			return;
		}
		bframes = new ImageResource[r.length];
		int nframes = r.length;

		for (int i = 0; i < nframes; i++) {
			// NOTE: Changed Transparency.BITMASK to Transparency.TRANSLUCENT
			bframes[i] = createImageResource(r[i], Transparency.TRANSLUCENT, r[i]
					.getWidth(null), r[i].getHeight(null), 0, 0);
		}
		imgRscCache.put(name, Collections.unmodifiableList(Arrays
				.asList(bframes)));

	}

	/**
	 * Gets the set of all stored image resources.
	 * 
	 * @return a <code>Set</code> of the resource names
	 * 
	 * @see #getFrames(String)
	 */
	public Set<String> imgResources() {
		return imgRscCache.keySet();
	}

	/**
	 * Gets the set of all stored audio clip resources.
	 * 
	 * @return a <code>Set</code> of the resource names
	 * 
	 * @see #loadAudioClip(String)
	 */
	public Set<String> audioResources() {
		return audioClipCache.keySet();
	}

	/**
	 * @return the number of resources being stored.
	 */
	public int size() {
		return audioClipCache.size() + imgRscCache.size() + fontCache.size();
	}
	
	/**
	 * Searches the resource caches for the specified name and removes that
	 * entry from the cache. If the name appears in more than one cache, a
	 * warning message is sent to the package logger.
	 * 
	 * 
	 * @param rscName
	 *            the name of the resource to remove
	 * @return <code>true</code> iff a resource was removed
	 */
	public boolean freeResource(final String rscName) {
		int n = 0;

		if (imgRscCache.remove(rscName) != null) {
			n++;
		}
		if (audioClipCache.remove(rscName) != null) {
			n++;
		}
		if (fontCache.remove(rscName) != null) {
			n++;
		}
		
		if (n > 1) {
			gameLog.info("Removed '" + rscName + "' from multiple caches.");
		}

		return (n > 0);
	}
	

	/**
	 * Creates a container for displaying the game.
	 * 
	 * @param title
	 *            the name to display on the frame (if applicable)
	 * @param graphicsConfig
	 *            a JIG graphics configuration
	 * 
	 * @return a container within which the game can be rendered
	 */
	public abstract GameFrame getGameFrame(String title, final int w, final int h,
			final boolean preferredFullScreen);


	
	/**
	 * Gets an audio resource name from our internal stash.
	 * 
	 * @return a name of a known audio resource.
	 */
	private String nextErsatzClipRsc() {
		clipsRequested++;
		clipsRequested %= NCLIPS;
		
		switch (clipsRequested) {
		case 0:
			return "jig/resources/click.wav";
		default:
			return "jig/resources/bounce.wav";
		}
	}

	
	/**
	 * Searches for the resource url given a resource name.
	 * One of the following results is possible:
	 * 
	 * <ul>
	 * <li>
	 * If the resource is found on the qualified path, the
	 * url is returned.
	 * </li>
	 * 
	 * <li>
	 * If the resource is not found on the qualified path, but
	 * a search through the classpath locates it, a warning
	 * is issued, and the url is returned</li>
	 * 
	 * <li>
	 * If the resource cannot be found even after a search,
	 * an additional info message is issued, and null is returned
	 * </li>
	 * </ul>
	 * 
	 * @param rscName
	 *            the name of the resource
	 * @return a valid <code>URL</code> if a resource is found,
	 *         <code>null</code> otherwise
	 */
	public static final URL findResource(final String rscName) {

		URL u = ClassLoader.getSystemResource(rscName);

		if (u != null) {
			return u;
		}

		gameLog.warning("Couldn't find '" + rscName + "' on qualified path.");

		for (StackTraceElement ste : new Throwable().getStackTrace()) {
			if (!ste.getClassName().startsWith("jig.engine")) {
				try {
					Class<?> callingObjClass = 
						Class.forName(ste.getClassName());
					u = callingObjClass.getResource(rscName);
					gameLog.fine("  - searching from location of class: "
							+ ste.getClassName());

				} catch (ClassNotFoundException cnfe) {
					continue;
				}

				if (u != null) {
					gameLog.warning("Found resource '" + rscName
							+ "' by inferring path. This is fragile;"
							+ " use a fully qualified path for your release.");
					break;
				}
			}
		}
		if (u == null) {
			gameLog.info("Couldn't locate resource '" + rscName + "'");
		}

		return u;
	}

	/**
	 * Loads a series of zero or more frames associated with the specified URL.
	 * Once retrieved, singleton references are stored in the ResourceFactory
	 * until explicitly released. This means it is easy for multiple objects to
	 * reuse the same image resource such reference sharing does not need to be
	 * managed by the game objects themselves. Calls
	 * <code>createImageResource</code> to ensure that the internal format of
	 * the concrete ResourceFactory class is applied. Prints a warning
	 * if the resource name is already in use.
	 * 
	 * @param u
	 *            a URL specifying the source of the image frames
	 * @param key
	 *            the name that this resource will be cached under
	 * @return <code>true</code> if the frames were already loaded, or were
	 *         successfully loaded on this invocation.
	 * 
	 * @see #getFrames(URL)
	 * 
	 */
	private boolean loadFrameResource(final URL u, final String key) {

		List<ImageResource> cachehit;
		ImageResource[] bframes;
		Matcher mat;

		if (imgRscCache.get(key) != null) {
			// already loaded
			jigLog.info("Resource '" + key + "' already loaded.");
			return true;
		}
		if (u == null) return false;

		BufferedImage originalImage;
		try {

			originalImage = ImageIO
					.read(new BufferedInputStream(u.openStream()));

		} catch (IOException e) {
			originalImage = null;
		}

		if (originalImage == null) {

			jigLog.warning("Could not load image from: " + u.toString());
			return false;
		}

		int rows = 1;
		int cols = 1;
		int nframes = 1;

		mat = RCF_FRAME_FILE_PATTERN.matcher(u.toString());
		// can we autorecognize frames in this file?
		if (mat.matches()) {
			rows = Integer.parseInt(mat.group(1));
			cols = Integer.parseInt(mat.group(2));
			nframes = Integer.parseInt(mat.group(3));
		}

		// create an array of image frames
		bframes = new ImageResource[nframes];

		// determine frame size
		int w = originalImage.getWidth() / cols;
		int h = originalImage.getHeight() / rows;

		// now create the frames.
		// basically, just make an array of BufferedImages
		// for each frame, paint the 'master' image into the
		// frame using an offset so that all but the appropriate
		// frame will be clipped.
		for (int i = 0; i < nframes; i++) {
			bframes[i] = createImageResource(originalImage,
					Transparency.TRANSLUCENT, w, h, w * (i % cols), h * (i / cols));
		}
		cachehit = Collections.unmodifiableList(Arrays.asList(bframes));
		imgRscCache.put(key, cachehit);
		return true;
	}



	/**
	 * Loads sprite images using an image and its associated xml file, which
	 * contains metadata about the image.
	 * 
	 * @param sheetName Path of the image to be loaded
	 * @param xmlName Path of the xml file describing the image
	 * @return <code>true</code> if no unrecoverable errors occurred.
	 */
	public boolean loadSheet(final String sheetName, final String xmlName) {

		XMLBasedLoader loader = new XMLBasedLoader();
		return loader.loadSpriteSheet(sheetName, xmlName);
	}
	
	/**
	 * Removes sprite resources from the resource factory cache.
	 * 
	 * @param sheetName Path of the image
	 * @param xmlName Path of the xml file describing the image
	 * @return <code>true</code> if no unrecoverable errors occurred.
	 */
	public boolean freeSheet(final String sheetName, final String xmlName) {

		XMLBasedLoader loader = new XMLBasedLoader();
		return loader.unloadSpriteSheet(sheetName, xmlName);
	}
	

	/**
	 * A helper class to load sprite sheets and resources based on information
	 * found in associated XML files. XML schema files are used to validate the
	 * files.
	 * 
	 * The functionality is a bit too complex to fit into a few methods
	 * inside ResourceFactory, and there is a possibility that this will become
	 * a first class object at some point; for now, however, an inner class will
	 * provide the encapsulation we want.
	 * 
	 * @author Scott Wallace
	 * @author Andrew Nierman
	 * 
	 */
	final class XMLBasedLoader {

		/** The image containing all of the sprites. */
		BufferedImage sheetImage;

		/** Height of the sheet. */
		int sheetHeight;

		/** Width of the sheet. */
		int sheetWidth;

		/**
		 * For internal class use only, this is a buffer that the parser fills
		 * with an integer. It is only valid after a successful call to
		 * <code>readXXXInteger</code>.
		 */
		private int parsedInt;

		private String xmlRoot;
		
		/**
		 * For internal class use only, this is a buffer that the parser fills
		 * with a string. It is only valid after a successful call to
		 * <code>readXXXString</code>.
		 */
		private String parsedString;


		/**
		 * Unload a sprite sheet.
		 * 
		 * @param sheetName the name of the sheet
		 * @param xmlName the name of the associated XML data file
		 * @return <code>true</code> if no unrecoverable errors occurred.
		 */
		boolean unloadSpriteSheet(final String sheetName, final String xmlName) {
			URL xmlURL = findResource(xmlName);
			return parseAndUnloadSpritesheet(sheetName, xmlURL);			
		}

		/**
		 * Load a sprite sheet.
		 * 
		 * @param sheetName
		 *            the name of the sheet, which will be mapped to a url.
		 * @param xmlName
		 *            the name of the associated XML data file,
		 *            which will also be mapped to a URL.
		 * @return <code>true</code> if no unrecoverable errors occurred.
		 * 
		 */
		boolean loadSpriteSheet(final String sheetName, final String xmlName) {

			URL sheetURL = findResource(sheetName);
			URL xmlURL = findResource(xmlName);

			if (!loadSpriteSheetImage(sheetURL)) {
				// in case another sheet was previously loaded...
				sheetHeight = -1;
				sheetWidth = -1;
				sheetImage = null;
			}
			
			return parseAndLoadSpritesheet(sheetName, xmlURL);
		}

		/**
		 * Loads the sprite sheet image.
		 * 
		 * @param sheetURL the url of the image
		 * @return <code>true</code> if the image loads successfully.
		 */
		private boolean loadSpriteSheetImage(final URL sheetURL) {
			if (sheetURL == null) {
				return false;
			}

			try {
				sheetImage = ImageIO.read(new BufferedInputStream(sheetURL
						.openStream()));

			} catch (IOException e) {
				sheetImage = null;
				gameLog.warning("IO Exception while loading image '"
						+ sheetURL.toString() + "'");
				return false;
			}
			
			/* Commented out by Travis Hall */
			/* PNGs may possibly be loaded in as translucent which is not
			* hardware accelerated. So if thats the case for right now lets
			* just convert it to BITMASK transparency */
//			if(sheetImage.getTransparency() == Transparency.TRANSLUCENT)
//			{
//				BufferedImage newSheet = new BufferedImage(sheetImage.getWidth(),
//						sheetImage.getHeight(), Transparency.BITMASK);
//	
//				newSheet.getGraphics().drawImage(sheetImage, 0, 0, null);
//				sheetImage = newSheet;
//			}

			sheetHeight = sheetImage.getHeight();
			sheetWidth = sheetImage.getWidth();
			return true;
		}
		
		/**
		 * Parses the Sprite sheet's xml file and unloads the resources.
		 * 
		 * @param keyRoot
		 *            usually the sheet name, more generally the root of the key
		 *            used to store these framesets
		 * @param xmlURL
		 *            the url of the xml data file
		 * @return <code>true</code> iff only recoverable errors (or no
		 *         errors) occurred during parsing.
		 */
		private boolean parseAndUnloadSpritesheet(final String keyRoot,
				final URL xmlURL) {
			if (xmlURL == null) {
				return false;
			}

			Document doc = validateXML(xmlURL, "spriteset.xsd");
			
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			
			NodeList framesets = root.getElementsByTagName("frameset");
			
			boolean framesetsOk = true;
			for (int i = 0; i < framesets.getLength(); ++i) {
				framesetsOk &= parseFrameset(keyRoot, framesets, i, false);
			}
			
			return framesetsOk;
		}
		
		/**
		 * Parses the Sprite sheet's xml file.
		 * 
		 * @param keyRoot
		 *            usually the sheet name, more generally the root of the key
		 *            used to store these framesets
		 * @param xmlURL the url of the xml data file
		 * @return <code>true</code> iff only recoverable errors (or no
		 *         errors) occurred during parsing.
		 */
		private boolean parseAndLoadSpritesheet(final String keyRoot,
				final URL xmlURL) {
			if (xmlURL == null) {
				return false;
			}

			Document doc = validateXML(xmlURL, "spriteset.xsd");

			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();

			// as specified by the schema, a transparent tag is optional.
			// if included it must be a hex number with 6 "digits", e.g., 00FF00
			if (readString((Element) root, "transparent")) {
				Color transparent = Color.decode("0x" + parsedString);
				makeSheetImageTransparent(transparent);
			}

			// at least one frameset element is required
			NodeList framesets = root.getElementsByTagName("frameset");

			boolean framesetsOk = true;
			for (int i = 0; i < framesets.getLength(); ++i) {
				framesetsOk &= parseFrameset(keyRoot, framesets, i, true);
			}
			
			return framesetsOk;
		}

		/**
		 * Parses a frameset block and performs some error checking.
		 * May add messages to <code>loadFailureMsgs</code>.
		 * 
		 * @param keyRoot
		 *            typically the sheetName, but more generally the root of
		 *            the key used to store the frameset
		 * @param framesets the node list of frameset blocks
		 * @param i the index of the frameset to parse
		 * @return <code>true</code> if the parse encounters only recoverable
		 *         errors, or no errors at all.
		 */
		private boolean parseFrameset(final String keyRoot,
				final NodeList framesets, final int i, final boolean load) {

			Node frameset = framesets.item(i);

			if (frameset.getNodeType() == Node.ELEMENT_NODE) {
				
				// frameset name, top, left, width and height are required
				// by the schema file (whereas row and column are optional)
				// width and height are required to be positive integers
				// name must be a non-empty string
				readFSString((Element) frameset, "name", i, true);
				String name = parsedString;

				readFSInteger((Element) frameset, "width", i, true);
				int width = parsedInt;
				
				readFSInteger((Element) frameset, "height", i, true);
				int height = parsedInt;

				String key = keyRoot + "#" + name;
				if (!load) {
					freeResource(key);
					return true;
				}
				
				// top and left are required to be non negative integers
				// by the schema file
				readFSInteger((Element) frameset, "top", i, true);
				int top = parsedInt;
				
				readFSInteger((Element) frameset, "left", i, true);
				int left = parsedInt;

				// rows and columns are optional, but when included,
				// must be positive integers, default value is set to 1:
				int rows = 1, columns = 1;

				if (readFSInteger((Element) frameset, "rows", 0, false)) {
					rows = parsedInt;
				}
				
				if (readFSInteger((Element) frameset, "columns", 0, false)) {
					columns = parsedInt;
				}
				
				boolean success = true;
				
				// error checking of dimensions
				if (top + height * rows > sheetHeight) {
					gameLog.warning("frameset " + i
							+ " extends past the height bounds of the image.");
					success = false;
				}
				if (left + width * columns > sheetWidth) {
					gameLog.warning("frameset " + i
							+ " extends past the width bounds of the image.");
					success = false;
				}

				
				if (imgRscCache.get(key) != null) {
					gameLog.info("Resource '" + key + "' already loaded.");
					return true;
				}

				if (!success || sheetImage == null) {
					// This is recoverable, but we need ersatz images.
					gameLog.fine("Using erstaz image for frameset.");
					PaintableCanvas.loadDefaultFrames(key,
								width, height, rows * columns, JIGSHAPE.CIRCLE, null);

				} else {
					gameLog.fine("Loading frameset from sprite sheet.");
					long st = System.nanoTime();

					ImageResource[] bframes = new ImageResource[rows * columns];
					for (int y = 0; y < rows; ++y) {
						for (int x = 0; x < columns; ++x) {
							//bframes[y * rows + x] = createImageResource(
							bframes[y * (columns) + x] = createImageResource(
									sheetImage, Transparency.TRANSLUCENT, width,
									height, left + width * x, top + height * y);
						}
					}		
					long t = ((System.nanoTime()-st)/(jig.engine.GameClock.NANOS_PER_MS));
					if (t > 500) {
						System.out.println("Created Image List with " + (rows*columns) + " elements in " + 
								t + " ms for resource " + key);
					}

					List<ImageResource> cachehit = Collections
							.unmodifiableList(Arrays.asList(bframes));
					imgRscCache.put(key, cachehit);
					gameLog.fine("Stored " + cachehit.size() + " frames under '"
							+ key + "'");
				}
				return true;
			} // if node type == ELEMENT_NODE

			return false;
		}
		
		/**
		 * Loads a image directly from a file into the resource cache.
		 * 
		 * Prints a warning if the resource name is already in use.
		 * 
		 * @param u
		 *            a URL specifying the source of the image frames
		 * @param key
		 *            the name that this resource will be cached under
		 * @return <code>true</code> if the frames were already loaded, or were
		 *         successfully loaded on this invocation.
		 * 
		 * @see #getFrames(URL)
		 * 
		 */
		private boolean loadFrameResource(final URL u, final String key) {

			List<ImageResource> cachehit;
			ImageResource bframe;

			if (imgRscCache.get(key) != null) {
				// already loaded
				gameLog.info("Resource '" + key + "' already loaded.");
				return true;
			}

			BufferedImage originalImage;
			try {

				originalImage = ImageIO
						.read(new BufferedInputStream(u.openStream()));

			} catch (IOException e) {
				originalImage = null;
			}

			if (originalImage == null) {

				gameLog.warning("Could not load image from: " + u.toString());
				return false;
			}

			int w = originalImage.getWidth();
			int h = originalImage.getHeight();
			
			bframe = createImageResource(originalImage,
						Transparency.TRANSLUCENT, w, h, 0, 0);
			
			cachehit = Collections.unmodifiableList(Arrays.asList(bframe));
			imgRscCache.put(key, cachehit);
			
			return true;
		}


		/**
		 * Recreates the sheet image with a special color used to indicate the
		 * transparent pixel.
		 * 
		 * @param transparent the color indicating 'transparent'
		 */
		private void makeSheetImageTransparent(final Color transparent) {
			if (transparent == null || sheetImage == null) {
				// nothing to do!
				return;
			}
			gameLog.finest("Transparent color is: " + transparent.toString());

			int x;
			int y;
			int t = transparent.getRGB();
			int clear = new Color(0, 0, 0, 0).getRGB();
			int set = 0;
			int orig;
			BufferedImage newImg = new BufferedImage(sheetWidth, sheetHeight,
					BufferedImage.TYPE_INT_ARGB);

			for (x = 0; x < sheetWidth; x++) {
				for (y = 0; y < sheetHeight; y++) {
					orig = sheetImage.getRGB(x, y);
					if (orig == t) {
						newImg.setRGB(x, y, clear);
						set++;
					} else {
						newImg.setRGB(x, y, orig);
					}
				}
			}
			gameLog.finest("Set " + set + " pixels to transparent color");
			sheetImage = newImg;
		}

		/**
		 * Retrieves the text from an XML node's child as a string. The result
		 * is stored in the member <code>parsedString</code> on success. On
		 * failure, there are no guarantees about the value stored in
		 * <code>parsedString</code>.
		 * 
		 * @param parentElement the XML element expected to contain the
		 * specified string element
		 * @param elementName the name of the child element from which to
		 * retrieve the text value
		 * @return <code>true</code> if elementName is found as a sub-element
		 */
		private boolean readString(final Element parentElement,
				final String elementName) {

			parsedString = null;
			NodeList nodeList = parentElement.getElementsByTagName(elementName);
			if (nodeList.getLength() > 0) {
				parsedString = nodeList.item(0).getTextContent().trim();
				return true;
			}
			return false;
		}

		/**
		 * Retrieves the text from an XML node's child as a string. The element
		 * is expected to be within a 'frameset' block. The result is stored in
		 * the member <code>parsedString</code> on success. On failure, there
		 * are no guarentees about the value stored in 
		 * <code>parsedString</code>.
		 * 
		 * @param parentElement
		 *            the XML element expected to contain the specified string
		 *            element
		 * @param elementName
		 *            the name of the child element from which to retrieve a
		 *            string
		 * @param frameset
		 *            the number of the frameset block being parsed
		 * @param required
		 *            <code>true</code> if a failure message should be added
		 *            to the list if this string cannot be found.
		 * @return <code>true</code> on success
		 */
		private boolean readFSString(final Element parentElement,
				final String elementName, final int frameset,
				final boolean required) {

			parsedString = null;
			NodeList nodeList = parentElement.getElementsByTagName(elementName);
			if (nodeList.getLength() > 0) {
				parsedString = nodeList.item(0).getTextContent().trim();
				return true;
			}
			if (required) {
				gameLog.warning("Couldn't find required '" + elementName
						+ "' tag in framset " + frameset);
			}
			return false;
		}

		/**
		 * Retrieves the text from an XML node's child as an integer. The
		 * element is expected to be within a 'frameset' block. The result is
		 * stored in the member <code>parsedInt</code> on success. On failure,
		 * there are no guarentees about the value stored in
		 * <code>parsedInt</code>.
		 * 
		 * @param parentElement
		 *            the XML element expected to contain the specified string
		 *            element
		 * @param elementName
		 *            the name of the child element from which to retrieve a
		 *            string
		 * @param frameset
		 *            the number of the frameset block being parsed
		 * @param required
		 *            <code>true</code> if a failure message should be added
		 *            to the list if this string cannot be found.
		 * @return <code>true</code> on success
		 */
		private boolean readFSInteger(final Element parentElement,
				final String elementName, final int frameset,
				final boolean required) {

			NodeList nodeList = parentElement.getElementsByTagName(elementName);
			if (nodeList.getLength() == 0) {
				if (required) {
					gameLog.warning("Couldn't find required '" + elementName
							+ "' tag in framset " + frameset);
				}
				return false;
			}

			String intStr = nodeList.item(0).getTextContent().trim();
			try {
				parsedInt = Integer.valueOf(intStr);
				return true;
			} catch (NumberFormatException nfe) {
				gameLog.warning("Couldn't interpret '" + intStr
						+ "' as an integer for '" + elementName + "' in "
						+ frameset);

			}
			return false;
		}
		
		/**
		 * Frees the resources specified by an XML file.
		 * 
		 * @param xmlPath the path to the resource xml file, ending with a
		 * directory separator
		 * @param xmlName the name of the resource xml file
		 * @return <code>true</code> if all resources are freed successfully
		 * 
		 */
		boolean unloadResourceSheet(final String xmlPath, final String xmlName) {
			xmlRoot = xmlPath;
			
			URL xmlURL = findResource(xmlPath + xmlName);
			
			// calling parse with a false argument will free resources:
			return parseXMLResourceFile(xmlURL, false);
		}
		
		/**
		 * Loads an XML file, parses it, and then loads the resources specified.
		 * 
		 * @param xmlPath the path to the resource xml file, ending with a
		 * directory separator
		 * @param xmlName the name of the resource xml file
		 * @return <code>true</code> if all resources are loaded successfully
		 */
		boolean loadResourceSheet(final String xmlPath, final String xmlName) {
			if (xmlPath.endsWith("/")) {
				xmlRoot = xmlPath;
			} else {
				xmlRoot = xmlPath + "/";
			}
			URL xmlURL = findResource(xmlRoot + xmlName);
			
			// calling parse with a true argument will load resources:
			return parseXMLResourceFile(xmlURL, true);
		}

		/**
		 * Parse an XML file and load (or unload) associated resources.
		 * 
		 * @param xmlURL the file's url
		 * @param xmlRoot the parent directory of the xml file
		 * @param load <code>true</code> if the resources 
		 *   should be loaded (<code>false</code> if they should be freed)
		 * @return <code>true</code> iff the file was parsed and processed
		 * correctly
		 */
		private boolean parseXMLResourceFile(final URL xmlURL, final boolean load) {
			if (xmlURL == null) {
				return false;
			}

			Document doc = validateXML(xmlURL, "resources.xsd");

			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			
			boolean spritesOk = processSpriteSheets(root.getElementsByTagName("spritesheet"), load); 
			boolean audioOk = processAudioClips(root.getElementsByTagName("audioclip"), load);
			boolean imagesOk = processImages(root.getElementsByTagName("image"), load);
			
			return spritesOk && audioOk && imagesOk;
		}
		
		/**
		 * Loads a list of sprite sheets.
		 * 
		 * @param elements a list of <code>&lt;spritesheet&gt;</code> elements
		 * @param load <code>true</code> if the resources 
		 *   should be loaded (<code>false</code> if they should be freed)
		 * @return <code>true</code> iff all the specified sprite sheets were
		 * loaded (or unloaded) successfully
		 */
		private boolean processSpriteSheets(final NodeList elements, 
				final boolean load) {
			
			for (int i = 0; i < elements.getLength(); i++) {
				// the schema file requires that spritesheet elements have a
				// single imagesrc sub-element and a single framesrc sub-element
				
				Node node = elements.item(i);
				
				NodeList subElements = ((Element) node).getElementsByTagName("imagesrc");
				String imgSrc = subElements.item(0).getTextContent();

				subElements = ((Element) node).getElementsByTagName("framesrc");
				String xmlSrc = subElements.item(0).getTextContent();
				
				if (load) {
					loadSheet(xmlRoot + imgSrc, xmlRoot + xmlSrc);
				} else {
					freeSheet(xmlRoot + imgSrc, xmlRoot + xmlSrc);
				}
			}
			
			return true;
		}

		/**
		 * Loads a list of audio clips.
		 * 
		 * @param elements a list of <code>&lt;audioclip&gt;</code> elements
		 * @param load <code>true</code> if the resources 
		 *   should be loaded (<code>false</code> if they should be freed)
		 * @return <code>true</code> iff all the specified audio clips were
		 * loaded (or unloaded) successfully
		 */
		private boolean processAudioClips(final NodeList elements,
				final boolean load) {

			for (int i = 0; i < elements.getLength(); i++) {
				// the schema file requires that audioclip elements have
				// a single src sub-element
				
				Node node = elements.item(i);
				
				NodeList subElements = ((Element) node).getElementsByTagName("src");
				String clipSource = subElements.item(0).getTextContent();

				if (load) {
					loadAudioClip(xmlRoot + clipSource);
				} else {
					freeResource(xmlRoot + clipSource);
				}
			}

			return true;
		}
		
		/**
		 * Loads a list of images.
		 * 
		 * @param elements a list of <code>&lt;image&gt;</code> elements
		 * @param load <code>true</code> if the resources 
		 *   should be loaded (<code>false</code> if they should be freed)
		 * @return <code>true</code> iff all the specified images were
		 * loaded (or unloaded) successfully
		 */
		private boolean processImages(final NodeList elements, 
				final boolean load) {

			for (int i = 0; i < elements.getLength(); i++) {
				// the schema file requires that image elements have a single
				// src sub-element
				
				Node node = elements.item(i);
				
				NodeList subElements = ((Element) node).getElementsByTagName("src");				
				String imageSource = subElements.item(0).getTextContent();
				
				if (load) {
					URL u = findResource(xmlRoot + imageSource);

					if (u == null || !loadFrameResource(u, xmlRoot + imageSource)) {
						gameLog.warning("Can't load resource name: " + xmlRoot + imageSource);
					}
				} else {
					freeResource(xmlRoot + imageSource);
				}				
			}
			return true;
		}
				
		
		private Document validateXML(final URL xmlURL, String schemaFile) {
			Document doc = null;
			
			try {
				DocumentBuilderFactory docBuilderFactory = 
					DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder;
				docBuilder = docBuilderFactory.newDocumentBuilder();

				doc = docBuilder.parse(xmlURL.openStream());
				
				// we provide a schema file to validate the xml sprite sheet
				SchemaFactory factory =
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				
				Schema schema = factory.newSchema(getClass().getResource(schemaFile));
				Validator validator = schema.newValidator();
			    validator.validate(new DOMSource(doc));
				
			} catch (SAXParseException e) {
				
				// NOTE[sw] (compatibility): Schema validation is broken in JDK 1.6.0_01
				// see java bug id: 6531160  
				// the result here will be an error message such as this:
				// cvc-elt.1: Cannot find the declaration of element 'spriteset'.
				// the bug is fixed in JDK 1.6.0_02
				gameLog.warning("Unable to parse/validate the XML " +
						xmlURL + " with resources.xsd. This can be caused by a known bug in Windows JDK 1.6.0_01");
				gameLog.warning(e.getMessage());
				gameLog.warning("Line Number: " + e.getLineNumber());
				return null;
			} catch (ParserConfigurationException e) {
				gameLog.warning("Parser Configuration Error.");
				gameLog.warning(e.getMessage());
				return null;
			} catch (SAXException e) {
				gameLog.warning("Parsing Error.");
				gameLog.warning(e.getMessage());
				return null;
			} catch (IOException e) {
				gameLog.warning("IO Error while reading '" + xmlURL.toString()
						+ "'");
				gameLog.warning(e.getMessage());
				return null;
			}
			
			return doc;
		}

	} //class XMLLoader
	
}


/**
 * A BitMap Font class that can be used to draw text onto 
 * the Game Frame. Instances of this class should not be used
 * directly by the game designer. Rather, fonts should be 
 * loaded as FontResources using the ResourceFactory.
 * 
 * 
 * @author Scott Wallace
 */
final class BitmapFont implements FontResource {

	static final int GUESS_FIRST_CHARACTER = -1;	
	private static final int ASCII_EXCLAMATION = 33;
	private static final int ASCII_SPACE = 32;
	
	private static final int BITMAP_FONT_WRITER_LENGTH = 58;
	private static final int FULL_PRINTABLE_CHAR_SET = 95;
	private static final int SPACE_THROUGH_UCZ = 59;
	
	int asciiOffset;
	int nCharacters;
	int[] xextents;
	
	private int baseline;
	private int lineHeight;

	List<ImageResource> frames;
	
	/**
	 * Private, use the static factory methods.
	 */
	private BitmapFont() { }

	/**
	 * Initialize the Bitmap Font. Guess the first character
	 * unless it's known.
	 * 
	 * @param nchars the number of characters in the font image.
	 * @param firstASCIIChar the first character or 
	 * <code>GUESS_FIRST_CHARACTER</code>
	 */
	private void init(final int nchars, final int firstASCIIChar) {
		xextents = new int[nchars];
		nCharacters = nchars;
		
		if (firstASCIIChar == GUESS_FIRST_CHARACTER) {
			switch (nCharacters) {
			
			case BITMAP_FONT_WRITER_LENGTH:
				// for some reason this scheme doesn't include the space
				asciiOffset = ASCII_EXCLAMATION;
				break;
			case SPACE_THROUGH_UCZ:
				// probably space, numbers some punctuation and upper-case
				asciiOffset = ASCII_SPACE;
				break;
				
			case FULL_PRINTABLE_CHAR_SET: 
				// should be ' '(32) through '~'(126) (everything)
				asciiOffset = ASCII_SPACE;
				break;

			default:
				System.err.println("Hmm. found " + nchars + " characters?");
			break;
			}
		} else {
			asciiOffset = firstASCIIChar; 
		}
		
	}
	
	/**
	 * Creates ImageResources to hold each character. It may be better to just
	 * store the whole image and when rendering just draw clipped portions as
	 * needed, but this should be tested.
	 * 
	 * @param fontImage
	 *            the image containing all font characters
	 * @param width
	 *            the width of the fontImage
	 * @param height
	 *            the height of the fontImage
	 * @param xoffsets
	 *            an array of offset information indicating where each character
	 *            can be found in the fontImage
	 * @param yoffset
	 *            an offset in the fontImage indicating the top of the line/font
	 * @param backgroundARGB
	 *            the color that the background of the font should be.
	 * 
	 * 
	 */
	private void createFrames(final BufferedImage fontImage,
			final int width,
			final int height, final int[] xoffsets, 
			final int yoffset, final int backgroundARGB) {
		
		ImageResource[] r;
		ResourceFactory f = ResourceFactory.getFactory();
		int mappingOffset;
		
		if (asciiOffset == ASCII_EXCLAMATION) {
			int widestWidth = 0;
			nCharacters++;
			asciiOffset--;
			
			int[] newExtents = new int[nCharacters];
			for (int i = 1; i < nCharacters; i++) {
				newExtents[i] = xextents[i - 1];
				if (newExtents[i] > widestWidth) {
					widestWidth = newExtents[i];
				}
			}
			newExtents[0] = widestWidth;
			xextents = newExtents;

			BufferedImage space = new BufferedImage(
					width, height, BufferedImage.BITMASK);

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					space.setRGB(x, y, backgroundARGB);
				}
			}

			r = new ImageResource[nCharacters];
			
			// the space image has no yoffset nor a xoffset since it 
			// is already the correct size.
			r[0] = f.createImageResource(space, BufferedImage.BITMASK,
					widestWidth, height, 0, 0);

			for (int i = 1; i < nCharacters; i++) {
				r[i] = f.createImageResource(fontImage, 
						BufferedImage.BITMASK,
						xextents[i], height, 
						xoffsets[i - 1], yoffset);				
			}
			
		} else {
			r = new ImageResource[nCharacters];
			mappingOffset = 0;
			for (int i = mappingOffset; i < nCharacters; i++) {
					r[i] = f.createImageResource(fontImage, 
							BufferedImage.BITMASK,
							xextents[i], height, xoffsets[i], yoffset);
			}
		}
				
		frames = Collections.unmodifiableList(Arrays
				.asList(r));
		
		return;
	}
	

	/**
	 * Loads a Bitmap FontWriter Font. This method should typically be called
	 * indirectly through the <code>ResourceFactory</code>.
	 * 
	 * A FontWriter Font is simply a font that is created using a bitmap image.
	 * Each character is represented by a frame.
	 * 
	 * The bitmap images height is 
	 * the (font height + 1) the first row is used to identify start
	 * and end points of each character/frame using a colored pixel
	 * which contrasts with the background (other pixels in that row).
	 * Each character's frame is exclusive of its boundaries. A 
	 * FontWriter Font's baseline is at the bottom of the image, so 
	 * characters CANNOT have a descent.
	 * 
	 * TODO: There appears to be a rendering artifact when using a background
	 * color with these fonts
	 * 
	 * @param rsc the name of the font resource
	 * @param transparentBackground <code>true</code> iff the font
	 * should be loaded so as to have a transparent background.
	 * @return a BitmapFont instance 
	 * 
	 * @see ResourceFactory#loadFontResource(String, boolean)
	 */
	static BitmapFont loadFontWriterFont(final String rsc, 
			final boolean transparentBackground) {
		
		URL u = ResourceFactory.findResource(rsc);
		BufferedImage img = null;
		
		if (u == null) {
			// no resource...
			return null;
		}
		
		try {
			img = ImageIO.
				read(new BufferedInputStream(u.openStream()));
		} catch (IOException e) { 
			ResourceFactory.getJIGLogger().warning(e.toString());
			return null;
		} 
		if (img == null) {
			ResourceFactory.getJIGLogger().warning("Couldn't load font: " 
					+ rsc); 
			return null;
		}
		BitmapFont font = new BitmapFont();
		
		
		// the upper left corner pixel should be a boundary, and its bottom
		// neighbor should be a background pixel
		int dividerRGB = img.getRGB(0, 0);
		int backgroundRGB = img.getRGB(0, 1);
		
		// make sure these pixels have a different color 
		if (dividerRGB == backgroundRGB) {
			ResourceFactory.getJIGLogger().warning(rsc 
					+ " is not a Bitmap Font Writer formatted font...");
		}
		// count characters to guess which character the font begins with...
		int x;
		int y;
		int height = img.getHeight();
		int width = img.getWidth();
		for (x = 1; x < width; x++) {
			if (img.getRGB(x, 0) == dividerRGB) {
				font.nCharacters++;
			}
		}

		font.init(font.nCharacters, GUESS_FIRST_CHARACTER);
		font.lineHeight = height - 1;
		font.baseline = height - 1; // baseline is the bottom of the font.
		
		
		int c = 0;
		int lastx = 1;
		int widestWidth = 0;
		int[] xoffsets = new int[font.nCharacters];
		
		// we know where the first character begins
		xoffsets[0] = lastx;
		for (x = 1; x < width; x++) {
			if (img.getRGB(x, 0) == dividerRGB) {
				font.xextents[c] = x - lastx;
				if (font.xextents[c] > widestWidth) {
					widestWidth = font.xextents[c];
				}
				c++;
				if (c == font.nCharacters) {
					break;
				}
				lastx = x + 1;
				xoffsets[c] = lastx;
			}
		}
		if (transparentBackground) {
			int clear = new Color(0, 0, 0, 0).getRGB();
			BufferedImage img2 = new 
				BufferedImage(width, height, BufferedImage.BITMASK);

			for (x = 0; x < width; x++) {
				for (y = 0; y < height; y++) {
					if (img.getRGB(x, y) == backgroundRGB) {
						img2.setRGB(x, y, clear);
					} else {
						img2.setRGB(x, y, img.getRGB(x, y));
					}
				}
			}
			font.createFrames(img2, width, height, xoffsets, 1, clear);

		} else {
			font.createFrames(img, width, height,
					xoffsets, 1, backgroundRGB);
		}
				
		return font;
		
	}

	/**
	 * Builds a new BitmapFont object from an existing TrueType or
	 * Adobe Type 1 system font. See the Java documentation for
	 * how to find fonts located in non-standard locations. Note
	 * that some system fonts are proprietary and there may be 
	 * limitations on how bitmap versions of these fonts 
	 * may be distributed.
	 * 
	 * TODO: This method appears to have visual bugs for some letters and some
	 * fonts For example 't's look chopped off with Sans Serif sometimes
	 * 
	 * @param systemFont the font to turn into a BitmapFont
	 * @param foreground the foreground (font) color
	 * @param background the background color ( Color(0, 0, 0, 0) for transparent )
	 * @return a new BitmapFont
	 * 
	 * @see java.awt.Font
	 */
	static BitmapFont buildBitmapFont(final Font systemFont, 
			final Color foreground, final Color background) {
		
		BufferedImage tmpGlyphImage;
		Graphics2D g;
		GlyphVector gv;
		StringBuffer alphabet;
		char[] oneLetter = new char[1];
		Rectangle2D glyphBounds;
		Rectangle2D maxCharBounds;
		int nchars = FULL_PRINTABLE_CHAR_SET;
		int height;
		int width;
		ResourceFactory f = ResourceFactory.getFactory();
		
		alphabet = new StringBuffer(100);
		for (int i = ASCII_SPACE, n = ASCII_SPACE + nchars; i < n; i++) {
			alphabet.append((char) i);
		}

		// guess about the size required to fit in one character
		// and set up the temp image.
		height = systemFont.getSize() * 4;
		width = systemFont.getSize() * 4;
		tmpGlyphImage = new BufferedImage(width, 
				height, BufferedImage.TYPE_INT_ARGB);
		
		// check to see if the guess is satisfactory...
		// if not, recreate the image...
		g = tmpGlyphImage.createGraphics();
		maxCharBounds = systemFont.getMaxCharBounds(g.getFontRenderContext());
		
		if (maxCharBounds.getWidth() > width 
				|| maxCharBounds.getHeight() > height) {
			
			// recreate the image so it's big enough...
			tmpGlyphImage = new BufferedImage(
					(int) maxCharBounds.getWidth() * 2,
					(int) maxCharBounds.getHeight() * 2,
					BufferedImage.TYPE_INT_ARGB);
			g.dispose();
			g = tmpGlyphImage.createGraphics();
			width = (int) maxCharBounds.getWidth() * 2;
			height = (int) maxCharBounds.getHeight() * 2;
		}
		
		
		ImageResource[] r = new ImageResource[nchars];
		BitmapFont font = new BitmapFont();
		font.init(nchars, ASCII_SPACE);
		
		g.setBackground(background);
		g.setColor(foreground);
		
		for (int i = 0, n = alphabet.length(); i < n; i++) {
			alphabet.getChars(i, i + 1, oneLetter, 0);
			// create a glyph for each letter
			gv = systemFont.createGlyphVector(g.getFontRenderContext(),
					oneLetter);
			g.clearRect(0, 0, width - 1, height - 1);
			
			glyphBounds = gv.getLogicalBounds();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
			// usually minX will be zero, but a 'j' for example, may be just < 0
			g.drawGlyphVector(gv, (float) -glyphBounds.getMinX(), 
					(float) -glyphBounds.getMinY()); 
			
			if (glyphBounds.getWidth() == 0) {
				ResourceFactory.getJIGLogger().warning("Font: " 
						+ systemFont.getFontName() 
						+ " reports zero width character '" + oneLetter[0] 
						+ "' using max width...");
				r[i] = f.createImageResource(tmpGlyphImage, 
						BufferedImage.BITMASK,
						(int) maxCharBounds.getWidth(), 
						(int) Math.ceil(glyphBounds.getHeight()), 0, 0);

				font.xextents[i] = (int) maxCharBounds.getWidth();
			} else {
				// DESIGN: SW --> AN: hashing on BuffImg in createImageResource?
				// I'm reusing the tmpGlyphImage, so that may be a problem
				r[i] = f.createImageResource(tmpGlyphImage, 
						BufferedImage.BITMASK,
						(int) Math.ceil(glyphBounds.getWidth()), 
						(int) Math.ceil(glyphBounds.getHeight()), 0, 0);

				font.xextents[i] = (int) glyphBounds.getWidth();
			}
		}
		font.baseline = (int) -maxCharBounds.getMinY();
		font.lineHeight = (int) maxCharBounds.getHeight();
		font.frames = Collections.unmodifiableList(Arrays
				.asList(r));

		return font;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void render(final String s, 
			final RenderingContext rc, final AffineTransform at) {
		int c;
		AffineTransform t = new AffineTransform(at);
		for (int i = 0, n = s.length(); i < n; i++) {
			c = (s.charAt(i));
			//System.out.println( s + "  Char " + (char)c + " is " + (int)c);
			c -= asciiOffset; 
			if(c < 0 || c >= frames.size()) continue;
			frames.get(c).render(rc, t);
			t.translate(xextents[c], 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(String s, Graphics2D g, AffineTransform at) {
		int c;
		AffineTransform t = new AffineTransform(at);
		for (int i = 0, n = s.length(); i < n; i++) {
			c = (s.charAt(i));
			//System.out.println( s + "  Char " + (char)c + " is " + (int)c);
			c -= asciiOffset; 
			if(c < 0 || c >= frames.size()) continue;
			frames.get(c).draw(g, t);
			t.translate(xextents[c], 0);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getStringWidth(final String s) {
		/* TODO: potentially move into a method that renders as well */
		int length = 0;
		int c;
		for (int i = 0, n = s.length(); i < n; i++) {
			c = (s.charAt(i));
			c -= asciiOffset; 
			length += xextents[c];
		}
		return length;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getCharWidth(char character) {
		int c = (int)character;
		
		if (c < asciiOffset) return 0;
		c -= asciiOffset;
		if (c > nCharacters) return 0;
		return xextents[c];
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getHeight() {
		return lineHeight;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getBaseline() {
		return baseline; 
	}
	
	/**
	 * Gets a string 'key' for specified parameters that may
	 * be passed to the factory methods. This method is 
	 * intended to be 
	 * used to store a font in a cache, and then later query
	 * for its existence.
	 * 
	 * @param rscName the name of the string resource (image file)
	 * @param transparentBackground <code>true</code> if the font's
	 * background should be made transparent.
	 * 
	 * @return a string representation of the resource name
	 * and parameters used by the factory methods (i.e., whether or
	 * not the background is transparent). 
	 * 
	 * 
	 */
	public static String fontKey(final String rscName, 
			final boolean transparentBackground) {

		return rscName + "-[" + Boolean.toString(transparentBackground)
		 + "]";
	}

	
	
}



