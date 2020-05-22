package com.example.bakingapp.api;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.bakingapp.model.Ingredient;
import com.example.bakingapp.model.Recipe;
import com.example.bakingapp.model.Step;
import com.example.bakingapp.util.LiveDataCallAdapterFactory;
import com.example.bakingapp.util.LiveDataTestUtil;
import com.squareup.moshi.Moshi;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class BakingApiServiceTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private BakingApiService bakingApiService;

    private MockWebServer mockWebServer;

    @Before
    public void createService() throws IOException {
        mockWebServer = new MockWebServer();
        Moshi moshi = new Moshi.Builder().build();
        bakingApiService = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(BakingApiService.class);
    }

    @After
    public void stopService() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getRecipes() throws IOException, InterruptedException {
        enqueueResponse("baking.json");
        List<Recipe> recipes = LiveDataTestUtil.getValue(bakingApiService.getRecipes()).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/baking.json"));

        assertThat(recipes.size(), is(4));

        Recipe recipe = recipes.get(0);
        assertThat(recipe.id, is(1));
        assertThat(recipe.name, is("Nutella Pie"));
        assertThat(recipe.servings, is(8));
        assertThat(recipe.image, is(""));

        List<Ingredient> ingredients = recipe.ingredients;
        Ingredient ingredient = ingredients.get(0);
        assertThat(ingredient.quantity, is(2.0));
        assertThat(ingredient.measure, is("CUP"));
        assertThat(ingredient.ingredient, is("Graham Cracker crumbs"));

        Recipe recipe2 = recipes.get(1);
        assertThat(recipe2.name, is("Brownies"));

        List<Step> steps = recipe2.steps;
        Step step = steps.get(0);
        assertThat(step.id, is(0));
        assertThat(step.shortDescription, is("Recipe Introduction"));
        assertThat(step.description, is("Recipe Introduction"));
        assertThat(step.videoURL, is("https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4"));
        assertThat(step.thumbnailURL, is(""));
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, Collections.emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
