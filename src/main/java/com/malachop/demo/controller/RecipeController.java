package com.malachop.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.malachop.demo.model.Recipe;
import com.malachop.demo.repository.RecipeRepository;

@RestController
public class RecipeController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	RecipeRepository recipeRepository;

	@GetMapping("/recipes")
	public ResponseEntity<Object> getAllRecipeNames() {
		logger.info("getAllRecipeNames()");

		List<Recipe> recipes = recipeRepository.getAllRecipes();
		List<String> recipeNames = new ArrayList<>();
		recipes.forEach(r -> recipeNames.add(r.getName()));

		Map<String, Object> body = new HashMap<>();
		body.put("recipeNames", recipeNames);
		return new ResponseEntity<Object>(body, HttpStatus.OK); // Status code 200
	}

	@GetMapping("/recipes/details/{name}")
	public ResponseEntity<Object> getRecipeDetails(@PathVariable("name") String name) {
		logger.info("getRecipeDetails()");

		Recipe recipe = recipeRepository.getRecipe(name);
		if (recipe != null) {

			Map<String, Object> body = new HashMap<>(), details = new HashMap<>();
			details.put("ingredients", recipe.getIngredients());
			details.put("numSteps", recipe.getIngredients().size());

			body.put("details", details);
			return new ResponseEntity<Object>(body, HttpStatus.OK);

		} else {
			return new ResponseEntity<Object>("{}", HttpStatus.OK);
		}
	}

	@PostMapping(value = "/recipes", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> addRecipe(@RequestBody Recipe recipe) {
		logger.info("addRecipe()");

		if(recipe.getName() == null || recipe.getName().isEmpty()) { // Status code 400
			return new ResponseEntity<Object>(generateErrorBody("Recipe name cannot be empty/null"), HttpStatus.BAD_REQUEST);
			
		} else if (recipeRepository.addRecipe(recipe)) { // No body, status code 201
			return new ResponseEntity<Object>(HttpStatus.CREATED);
			
		} else { // Status code 400
			return new ResponseEntity<Object>(generateErrorBody("Recipe already exists"), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "/recipes", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateRecipe(@RequestBody Recipe recipe) {
		logger.info("updateRecipe()");

		if(recipe.getName() == null || recipe.getName().isEmpty()) { // Status code 400
			return new ResponseEntity<Object>(generateErrorBody("Recipe name cannot be empty/null"), HttpStatus.BAD_REQUEST);
		
		} else if (recipeRepository.updateRecipe(recipe)) { // No body, status code 204
			return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
			
		} else { // Status code 404
			return new ResponseEntity<Object>(generateErrorBody("Recipe does not exist"), HttpStatus.NOT_FOUND);
		}
	}

	// Convenience method to generate json error response body
	private Map<String, Object> generateErrorBody(String message) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", message);
		return body;
	}

}
