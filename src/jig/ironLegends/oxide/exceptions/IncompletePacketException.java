package jig.ironLegends.oxide.exceptions;

/**
 * @author Travis Hall
 */
public class IncompletePacketException extends IronOxideException {
	private static final long serialVersionUID = 8673867875846814010L;

	public IncompletePacketException() {
		super("Packet is missing part of its data.");
	}
}
