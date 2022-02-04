package com.malachop.demo.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

@Service
public class ResourceLoaderService implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public boolean resourceExists(String path) {
		Resource resource = resourceLoader.getResource(path);
		return resource.exists();
	}

	public String readFile(String path) throws IOException {
		Resource resource = resourceLoader.getResource(path);
		Reader reader = new InputStreamReader(resource.getInputStream());
		return FileCopyUtils.copyToString(reader);
	}

	public void writeFile(String path, String content) throws IOException {
		Resource resource = resourceLoader.getResource(path);
		File file = resource.getFile();
		
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
	}
	
	public Resource getResource(String path) {
		return resourceLoader.getResource(path);
	}

}
