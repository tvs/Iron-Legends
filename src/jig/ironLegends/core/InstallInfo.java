package jig.ironLegends.core;

import java.io.File;
import java.net.URL;

public class InstallInfo 
{
	/*
	static public <TClass> String getPackageName(Class<TClass> mainClass)
	{
		String sPackagName = null;
		
		//Package pkg = ((Class<TClass>)mainClass).getPackage();
		Package pkg = mainClass.getPackage();
		if (pkg != null)
			return pkg.getName();
		return null;
	}
	*/
	
	//className="MPHedgeRunner.class"
	// jarName="MPHedgeRunner.jar"
	// classPath="mjpp/hedgeRunner/"
	static public String getInstallDir(String className, String jarName)//, String classPath)
	{
		
		//String ss = getPackageName(MPInstallInfo.class);
		String sInstallDir = "\\Temp";

		URL classURL = InstallInfo.class.getResource(className); 
		String s = classURL.getFile();
		System.out.println("className: "+ className);
		System.out.println("\tInstalled in file: " + s);
		
		File installDir = new File(s.substring(0, s.lastIndexOf("/")));
		
		sInstallDir = installDir.toString();
		if (sInstallDir.startsWith("file:"))
			sInstallDir = installDir.toString().substring(6);
		
		int bangIdx = sInstallDir.indexOf("!"); 
		if (bangIdx > 0)
		{
			// real installation
			System.out.println("Using Install Dir: " + sInstallDir);
			
			sInstallDir = sInstallDir.substring(0, bangIdx);
			String sPackage = jarName;//"MPHedgeRunner.jar";
			sInstallDir = sInstallDir.substring(0, sInstallDir.length()-sPackage.length());
			sInstallDir += "/";
		}
		else
		{
			//System.out.println("Debugger.");
			//System.out.println("Using Install Dir: " + sInstallDir);
			
			sInstallDir  += "\\";
			Package pkg = InstallInfo.class.getPackage();
			if (pkg != null)
			{
				String sPackage = pkg.getName();//"mjpp/hedgeRunner/";
				sInstallDir = sInstallDir.substring(0, sInstallDir.length()-sPackage.length());
			}
			else
			{
				//System.out.println("Package was null");
			}
		}
		//System.out.println("Installed dir=" + installDir);
		//System.out.println("Installed dir=" + sInstallDir);
		
		return sInstallDir;
	}
}
