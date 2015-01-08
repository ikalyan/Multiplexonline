package com.navyaentertainment.services;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SchedulerJob extends QuartzJobBean {
	
	protected static Logger logger = Logger.getLogger(SchedulerJob.class);
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(RTPSplitterConstant.COMPLETE_PATH_PROPERTY_FILE);
			properties.load(input);
			String uploadFileName = properties.getProperty(RTPSplitterConstant.UPLOADFILE);
			if(uploadFileName!=null && uploadFileName.trim().length()>0){
				System.out.println(uploadFileName);
				BufferedReader br = new BufferedReader(new FileReader(uploadFileName));
				try {
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();

					while (line != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
						line = br.readLine();
					}
					String everything = sb.toString();
					System.out.println(everything);
				} finally {
					br.close();
				}
			}
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

}
