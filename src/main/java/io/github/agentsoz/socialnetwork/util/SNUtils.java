package io.github.agentsoz.socialnetwork.util;

//import bushfire.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.agentsoz.socialnetwork.Network;
import io.github.agentsoz.socialnetwork.SNConfig;
import io.github.agentsoz.socialnetwork.SocialNetworkManager;
import io.github.agentsoz.socialnetwork.SocialAgent;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class SNUtils {


	static long  simTime = 0L;
	static long stepsize = 60L;
	static long  endSimTime = 61200L; // 8h=28800L, 61200 = 17 * 60min steps
	static String mainConfig = "./case_studies/hawkesbury/hawkesbury.xml";

	final static Logger logger = LoggerFactory.getLogger("");

	private static int lowCt = 0,medCt=0,highCt=0; // social state counters
	
	public static void stepTime() { 
		simTime = simTime +stepsize;
	}

	public static boolean isDiffTurn(long simTime) {
		boolean result;
		result = (simTime % SNConfig.getDiffturn() == 0) || simTime == 1.0; // simtime ==1 is needed to start reasoning in the BDI side when seed is set.
		logger.trace("isDiffTurn? {} {}",simTime, SNConfig.getDiffturn());
		return result;
	}

	public static long getSimTime() { 
		return simTime;
	}

	public static long getEndSimTime() { 
		return endSimTime;
	}

	public static void setEndSimTime(long newEndTime) {
		endSimTime = newEndTime;
	}
	
	public static void resetSimulationClock() { 
		simTime = 0L;
	}

	
	public static void readAndSetSNMainConfigs() { 
		//Config.setConfigFile(mainConfig);
		SNConfig.setConfigFile(mainConfig);
		SNConfig.readConfig();
		logger.trace("setting SN main configs complete");
	}
	
	public static void setMainConfigFile() {
		SNConfig.setConfigFile(mainConfig);
	}

	public static String getMainConfigFile() {
		return mainConfig;
	}
	
	public static void randomAgentMap(SocialNetworkManager sn_manager, int nodes, int cordRange) { 
		Random  random = new Random();
		
		for(int id=0; id < nodes; id++) {
			int x = random.nextInt(cordRange);
			int y = random.nextInt(cordRange);
			sn_manager.createSocialAgent(Integer.toString(id));sn_manager.setCords(Integer.toString(id),x,y);
		}
				
		
	}
	public static void createAgentMapUsingActualCoords(SocialNetworkManager snManager, int nodes) { 
		int idCount=0; // restricts the number of agent ids extracted from the .txt file
		BufferedReader br = null;
		FileReader fr = null;

		logger.info("setting up agent map with actual coordinates.....");

		
		try {

			fr = new FileReader(SNConfig.getAgentCoordFile());
			br = new BufferedReader(fr);

			String sCurrentLine;
			String[] coordsArray;
			
			br = new BufferedReader(new FileReader(SNConfig.getAgentCoordFile()));

			while ( (sCurrentLine = br.readLine()) != null && idCount < nodes) {
				logger.trace(sCurrentLine);
				coordsArray = sCurrentLine.split("\t");
				logger.trace("id: {} x: {} y: {}",coordsArray[0],coordsArray[1],coordsArray[2]);
				snManager.createSocialAgent(coordsArray[0]);
				snManager.setCords(coordsArray[0],Double.parseDouble(coordsArray[1]),Double.parseDouble(coordsArray[2]));

//				id++;
				idCount++;
			}

		} catch (IOException e) {
			logger.error("IO exception:");
			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

	}

	public static void createTestNetwork(String networkFile, SocialNetworkManager snman) {
		Network net =  new Network();
		File netFile = new File(networkFile);
		Scanner scan;
		try{
			scan = new Scanner(netFile);

			while(scan.hasNext())
			{
				net.createLinkWithGivenWeight(scan.nextInt(), scan.nextInt(), scan.nextDouble(), snman.getAgentMap());

			}
			scan.close();
			net.printAgentList(snman.getAgentMap()); // finally print
			net.printNetworkWegihts(snman.getAgentMap());
		}
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logger.error("test sn file not found ");

		}catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("Null pointer exception caught: {}", e.getMessage());
		}
	}

	public static void countLowMedHighAgents(SocialNetworkManager sn) {

		int l=0,m=0,h=0;
		for(SocialAgent agent:sn.getAgentMap().values()){

			String state = agent.getState();
			if(state.equals(DataTypes.LOW)) {
				l++;
			}
			else if(state.equals(DataTypes.MEDIUM)) {
				m++;
			}
			else if(state.equals(DataTypes.HIGH)){
				h++;
			}
		}

		setHighCount(h);
		setMedCount(m);
		setLowCount(l);



	}

	public static void setLowCount(int l) {
		lowCt = l;
	}

	public static void setMedCount(int m) {
		medCt = m;
	}

	public static void setHighCount(int h) {
		highCt = h;
	}

	public static int getLowCt() {
		return lowCt;
	}

	public static int getMedCt() {
		return medCt;
	}

	public static int getHighCt() {
		return highCt;
	}



}
