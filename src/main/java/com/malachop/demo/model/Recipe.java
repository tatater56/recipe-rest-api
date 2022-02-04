package com.malachop.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {

	private String name;
	private List<String> ingredients;
	private List<String> instructions;

	public Recipe(String name, List<String> ingredients, List<String> instructions) {
		this.name = name;
		this.ingredients = ingredients;
		this.instructions = instructions;
	}
	
	public Recipe() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<String> ingredients) {
		this.ingredients = ingredients;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<String> instructions) {
		this.instructions = instructions;
	}

	@Override
	public String toString() {
		return String.format("Recipe [name=%s, ingredients=%s, instructions=%s]", name, ingredients, instructions);
	}

}
