package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.LinkedList;

class RecipeImpl extends RecipeGrpc.RecipeImplBase {
    public LinkedList<Ingredient> ingredients = new LinkedList<>();
    public LinkedList<RecipeEntry> recipeEntries = new LinkedList<>();
    public RecipeImpl() {
        super();
        Ingredient.Builder addIngredient = Ingredient.newBuilder();
        addIngredient.setName("Salt");
        addIngredient.setQuantity(2);
        addIngredient.setDetails("2 gram of salt.");
        Ingredient finalIngredient1 = addIngredient.build();
        ingredients.add(finalIngredient1);

        addIngredient.setName("Salmon");
        addIngredient.setQuantity(300);
        addIngredient.setDetails("300 gram of Salmon.");
        Ingredient finalIngredient2 = addIngredient.build();
        ingredients.add(finalIngredient2);

        addIngredient.setName("Salad");
        addIngredient.setQuantity(100);
        addIngredient.setDetails("100 gram of Salad.");
        Ingredient finalIngredient3 = addIngredient.build();
        ingredients.add(finalIngredient3);

        addIngredient.setName("Water");
        addIngredient.setQuantity(500);
        addIngredient.setDetails("500 gram of Water.");
        Ingredient finalIngredient4 = addIngredient.build();
        ingredients.add(finalIngredient4);

        RecipeEntry.Builder addRecipe = RecipeEntry.newBuilder();
        addRecipe.setId(0);
        addRecipe.setName("Salmon Salad");
        addRecipe.setAuthor("Sheldon");
        addRecipe.setRating(5);
        addRecipe.addIngredient(ingredients.get(0));
        addRecipe.addIngredient(ingredients.get(1));
        addRecipe.addIngredient(ingredients.get(2));
        RecipeEntry finalRecipe = addRecipe.build();
        recipeEntries.add(finalRecipe);

        RecipeEntry.Builder addRecipe2 = RecipeEntry.newBuilder();
        addRecipe2.setId(1);
        addRecipe2.setName("Salmon Soup");
        addRecipe2.setAuthor("Sheldon");
        addRecipe2.setRating(5);
        addRecipe2.addIngredient(ingredients.get(0));
        addRecipe2.addIngredient(ingredients.get(1));
        addRecipe2.addIngredient(ingredients.get(3));
        RecipeEntry finalRecipe2 = addRecipe2.build();
        recipeEntries.add(finalRecipe2);
    }
    @Override
    public void addRecipe(RecipeReq req, StreamObserver<RecipeResp> responseObserver) {
        int size = recipeEntries.size();
        int id = size;
        String name = req.getName();
        String author = req.getAuthor();
        Ingredient ingredient = req.getIngredient(0);
        System.out.println("Received from client: " + name + " " + author + " " + ingredient.getName());
        RecipeResp.Builder response = RecipeResp.newBuilder();

        try {
            RecipeEntry.Builder addRecipe = RecipeEntry.newBuilder();
            addRecipe.setId(id);
            addRecipe.setName(name);
            addRecipe.setAuthor(author);
            addRecipe.setRating(0);
            addRecipe.addIngredient(ingredient);
            RecipeEntry finalRecipe = addRecipe.build();
            recipeEntries.add(finalRecipe);
            System.out.println("Recipe has been saved.");
            response.setIsSuccess(true);
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setError("Unable to add recipe.");
        }

        RecipeResp resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void viewRecipes(Empty req, StreamObserver<RecipeViewResp> responseObserver) {
        System.out.println("Received from client: request to view all recipes.");
        RecipeViewResp.Builder response = RecipeViewResp.newBuilder();

        for (RecipeEntry recipeEntry : recipeEntries) {
            response.addRecipes(recipeEntry);
        }

        RecipeViewResp resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
    @Override
    public void rateRecipe(RecipeRateReq req, StreamObserver<RecipeResp> responseObserver) {
        int id = req.getId();
        float rating = req.getRating();
        RecipeResp.Builder response = RecipeResp.newBuilder();

        try {
            RecipeEntry recipeEntry = recipeEntries.get(id);
            RecipeEntry.Builder addRecipe = RecipeEntry.newBuilder();
            addRecipe.setId(id);
            addRecipe.setName(recipeEntry.getName());
            addRecipe.setAuthor(recipeEntry.getAuthor());
            addRecipe.setRating(rating);
            for (int i = 0; i < recipeEntry.getIngredientCount(); i++) {
                addRecipe.addIngredient(recipeEntry.getIngredient(i));
            }
            RecipeEntry finalRecipe = addRecipe.build();
            recipeEntries.set(id, finalRecipe);

            response.setIsSuccess(true);
            response.setMessage("Rated " + recipeEntry.getName() + " with " + rating + "star.");
        } catch (Exception e) {
            response.setIsSuccess(false);
            response.setError("The id is not valid.");
            e.printStackTrace();
        }

        RecipeResp resp = response.build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}
