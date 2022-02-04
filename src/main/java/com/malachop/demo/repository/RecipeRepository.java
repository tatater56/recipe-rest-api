package com.malachop.demo.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.malachop.demo.model.Recipe;
import com.malachop.demo.service.ResourceLoaderService;

@Repository
public class RecipeRepository {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private static final String FS_DATA_PATH = "file:data.json";
	private static final String CP_DATA_PATH = "classpath:data.json";
	private static final boolean PREFER_CLASSPATH_DATA_PATH = false;

	@Autowired
	private ResourceLoaderService resourceLoaderService;

	private Map<String, Recipe> recipesMap = new HashMap<>();
	private ObjectMapper objectMapper = new ObjectMapper();
	
	
	
	// === Public interface === //
	
	public List<Recipe> getAllRecipes() {
		readFromFile();
		return new ArrayList<Recipe>(recipesMap.values());
	}

	public Recipe getRecipe(String name) {
		readFromFile();
		return recipesMap.get(name);
	}

	public boolean addRecipe(Recipe recipe) {
		readFromFile();
		boolean result;

		if (recipesMap.containsKey(recipe.getName())) {
			result = false;
		} else {
			result = true;
			recipesMap.put(recipe.getName(), recipe);
		}

		writeToFile();
		return result;
	}

	public boolean updateRecipe(Recipe recipe) {
		readFromFile();
		boolean result;

		if (recipesMap.containsKey(recipe.getName())) {
			result = true;
			recipesMap.put(recipe.getName(), recipe);
		} else {
			result = false;
		}

		writeToFile();
		return result;
	}
	
	
	
	// === File system I/O === //
	
	private void readFromFile() {
		Resource fsResource = resourceLoaderService.getResource(FS_DATA_PATH);
		if (!PREFER_CLASSPATH_DATA_PATH && fsResource.exists()) {

			logger.info("Reading recipe data file from filesystem.");
			try {
				JsonNode rootNode = objectMapper.readTree(fsResource.getFile());
				decodeJson(rootNode);
			} catch (IOException e) {
				logger.error("Encountered IOError while attempting to read data file from filesystem.");
				throw new UncheckedIOException(e);
			}

		} else {

			Resource cpResource = resourceLoaderService.getResource(CP_DATA_PATH);
			if (cpResource.exists()) {

				logger.info("Reading recipe data file from classpath.");
				try {
					// Use resource.getInputStream() for files inside jar
					JsonNode rootNode = objectMapper.readTree(cpResource.getInputStream());
					decodeJson(rootNode);
				} catch (IOException e) {
					logger.error("Encountered IOError while attempting to read data file from classpath.");
					throw new UncheckedIOException(e);
				}

			} else {
				logger.error("Recipe data file not found in classpath.");
			}

		}
	}

	private void decodeJson(JsonNode node) {
		logger.info("Decoding json into Recipe objects.");

		JsonNode recipesNode = node.get("recipes");
		if (recipesNode.isArray()) {

			recipesMap.clear();

			ArrayNode recipesArrayNode = (ArrayNode) recipesNode;
			for (JsonNode recipeNode : recipesArrayNode) {
				Recipe recipe = new Recipe();
				recipe.setName(recipeNode.get("name").asText());
				recipe.setIngredients(decodeArrayNode(recipeNode.get("ingredients")));
				recipe.setInstructions(decodeArrayNode(recipeNode.get("instructions")));

				recipesMap.put(recipe.getName(), recipe);
			}

		} else {
			logger.error("Encountered non-ArrayNode when trying to parse 'recipes' Node.");
		}
	}

	private List<String> decodeArrayNode(JsonNode node) {
		List<String> elements = new ArrayList<>();

		if (node.isArray()) {
			((ArrayNode) node).forEach(e -> elements.add(e.asText()));
		} else {
			logger.error("Encountered non-ArrayNode when trying to decode Node to List<String>.");
		}

		return elements;
	}

	private void writeToFile() {
		logger.info("Writing recipe data file to filesystem.");
		
		Resource resource = resourceLoaderService.getResource(FS_DATA_PATH);
		try {
			File file = resource.getFile();
			
			if(!file.exists()) {
				file.createNewFile();
			}
			
			ObjectNode rootNode = objectMapper.createObjectNode();
			rootNode.set("recipes", objectMapper.convertValue(recipesMap.values(), JsonNode.class));
			String content = rootNode.toString();
			
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			logger.error("Encountered IOException while attempting to write data file to filesystem.");
			throw new UncheckedIOException(e);
		}
	}

}
