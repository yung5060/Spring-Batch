package com.kbank.eai.springbatch;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableBatchProcessing
@EnableEncryptableProperties
public class SpringBatchApplication {

	public static void main(String[] args) throws Exception {
		
		loadJar(args[0]);
		
		SpringApplication.run(SpringBatchApplication.class, args);
	}
	
	private static void loadJar(String dir) throws Exception {
		try {
			log.info("jdbc path : " + dir);
			final URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			final Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
			method.setAccessible(true);
			
			new File(dir).listFiles(jar -> {
				log.info(jar.toString());
				if(jar.toString().toLowerCase().contains(".jar")) {
					try {
						method.invoke(loader, new Object[] {jar.toURI().toURL()});
						log.info(jar.getName() + " has been loaded.");
					} catch(Exception e) {
						e.printStackTrace();
						log.error(jar.getName() + " has failed loading.");
					}
				}
				return false;
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
