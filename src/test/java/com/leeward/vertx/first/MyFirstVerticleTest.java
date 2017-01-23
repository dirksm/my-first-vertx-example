package com.leeward.vertx.first;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fasterxml.jackson.databind.cfg.ConfigFeature;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest {
	
	private Vertx vertx;
	private int port = 8082;
	
	@Before
	public void setUp(TestContext context) {
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));
		vertx = Vertx.vertx();
		vertx.deployVerticle(MyFirstVerticle.class.getName(), options, context.asyncAssertSuccess());
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}
	
	@Test
	public void testMyApplication(TestContext context) {
		final Async async = context.async();
		
		vertx.createHttpClient().getNow(port, "localhost", "/", 
			response -> {
				response.handler(body -> {
					context.assertTrue(body.toString().contains("Hello"));
					async.complete();
				});
			});
	}
	
/**	@Test
	public void checkThatTheIndexPageIsServed(TestContext context) {
		final Async async = context.async();
		vertx.createHttpClient().getNow(port, "localhost","/assets/index.html", response -> {
			context.assertEquals(response.statusCode(), 200);
			context.assertEquals(response.headers().get("content-type"), "text/html");
			response.bodyHandler(body -> {
				context.assertTrue(body.toString().contains("Whiskey"));
				async.complete();
			});
		});
	}
	*/
}
