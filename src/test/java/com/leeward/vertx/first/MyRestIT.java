package com.leeward.vertx.first;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class MyRestIT {

	@BeforeClass
	public static void configureRestAssured() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Integer.getInteger("http.port", 8082);
	}
	
	@Test
	public void checkThatWeCanRetrieveIndividualProduct() {
		final int id = get("/api/whiskies").then()
				.assertThat()
				.statusCode(200)
				.extract()
				.jsonPath().getInt("find {it.name=='Bowmore 15 Years Laimrig' }.id");
		get("/api/whiskies/"+id).then()
		.assertThat()
		.statusCode(200)
		.body("name", equalTo("Bowmore 15 Years Laimrig"))
		.body("origin", equalTo("Scotland, Islay"))
		.body("id", equalTo(id));
	}
	
	@Test
	public void checkWeCanAddAndDeleteAProduct() {
	  // Create a new bottle and retrieve the result (as a Whisky instance).
	  Whisky whisky = given()
	      .body("{\"name\":\"Jameson\", \"origin\":\"Ireland\"}").request().post("/api/whiskies").thenReturn().as(Whisky.class);
	  assertThat(whisky.getName()).isEqualToIgnoringCase("Jameson");
	  assertThat(whisky.getOrigin()).isEqualToIgnoringCase("Ireland");
	  assertThat(whisky.getId()).isNotZero();
	  // Check that it has created an individual resource, and check the content.
	  get("/api/whiskies/" + whisky.getId()).then()
	      .assertThat()
	      .statusCode(200)
	      .body("name", equalTo("Jameson"))
	      .body("origin", equalTo("Ireland"))
	      .body("id", equalTo(whisky.getId()));
	  // Delete the bottle
	  delete("/api/whiskies/" + whisky.getId()).then().assertThat().statusCode(204);
	  // Check that the resource is not available anymore
	  get("/api/whiskies/" + whisky.getId()).then()
	      .assertThat()
	      .statusCode(404);
	}
	
	@AfterClass
	public static void unconfigureRestAssured() {
		RestAssured.reset();
	}
}
