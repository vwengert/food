package com.recipemanager.service;

import com.recipemanager.model.Recipe;
import com.recipemanager.repository.FoodRepository;
import com.recipemanager.repository.RecipeHeaderRepository;
import com.recipemanager.repository.RecipeRepository;
import com.recipemanager.util.exceptions.IdNotAllowedException;
import com.recipemanager.util.exceptions.NoContentException;
import com.recipemanager.util.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {
	private final RecipeRepository recipeRepository;
	private final RecipeHeaderRepository recipeHeaderRepository;
	private final FoodRepository foodRepository;

	@Override
	public List<Recipe> getRecipeByRecipeHeaderId(Long recipeHeaderId) throws NotFoundException {
		List<Recipe> recipeList = recipeRepository.findByRecipeHeaderId(recipeHeaderId);
		if (recipeList.isEmpty()) {
			throw new NotFoundException();
		}
		return recipeList;
	}

	@Override
	public Recipe putRecipe(Recipe recipe) throws NotFoundException {
		Recipe originalRecipe = recipeRepository.findById(recipe.getId()).orElseThrow(NotFoundException::new);

		checkIfRecipeHeaderAndFoodIdExistsOrElseThrowException(recipe);

		originalRecipe.setRecipeHeader(recipe.getRecipeHeader());
		originalRecipe.setFood(recipe.getFood());

		return recipeRepository.save(originalRecipe);
	}

	@Override
	public Recipe postRecipe(Recipe recipe) throws IdNotAllowedException, NotFoundException {
		if (recipe.getId() != null)
			throw new IdNotAllowedException();
		checkIfRecipeHeaderAndFoodIdExistsOrElseThrowException(recipe);

		return recipeRepository.save(recipe);
	}


	@Override
	public void delete(Long id) throws NoContentException {
		if (!recipeRepository.existsById(id))
			throw new NoContentException();

		recipeRepository.deleteById(id);
	}

	private void checkIfRecipeHeaderAndFoodIdExistsOrElseThrowException(Recipe recipe) throws NotFoundException {
		if (!recipeHeaderRepository.existsById(recipe.getRecipeHeader().getId()))
			throw new NotFoundException();
		if (!foodRepository.existsById(recipe.getFood().getId()))
			throw new NotFoundException();
	}
}
