package com.sbk.testdemo;


import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

import org.testng.annotations.BeforeTest;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;


class RestAPITest {
	
	private final Integer TEST_NUMBER = 8;
	private final String TEST_YEAR = "1888";
	
	
	@BeforeTest
	public void setup() {

		RestAssured.baseURI = "http://numbersapi.com";
		//RestAssured.port = 443;
		
		// Configure RESTAssured to handle text/plain type of response, which it does not handle by default.
		RestAssured.registerParser("text/plain", Parser.TEXT);
	}
	
	/**
	 * Gets trivia facts in plain text for a set for numbers
	 */
	@Test
	public void testGetTriviaForNumbers() {
		
		ArrayList<Integer> numList = new ArrayList<Integer>(
				Arrays.asList(1,1000,1999,2021,100000));
		
		for (Integer testNum : numList) {
			String preText = String.valueOf(testNum)+" is ";
			
			given().
			pathParam("numParm",testNum).
			when().
				get(RestAssured.baseURI+"/{numParm}").
			then().
				assertThat()
				.statusCode(200)
				.and()
				.contentType(ContentType.TEXT)
				.and()
		        .header("Server",equalTo("nginx/1.4.6 (Ubuntu)"))
		        .header("X-Powered-By",equalTo("Express"))
				.body(startsWith(preText)); 
		}
	}
	
	
	/**
	 * Gets trivia facts in plain text for a set for edge-case numbers
	 */
	@Test
	public void testGetTriviaForEdgeNumbers() {
		
		ArrayList<Integer> numList = new ArrayList<Integer>(
				Arrays.asList(Integer.MIN_VALUE,-1,0,1,Integer.MAX_VALUE));
		
		for (Integer testNum : numList) {
			String preText = String.valueOf(testNum)+" is ";
			
			given().
			pathParam("numParm",testNum).
			when().
				get(RestAssured.baseURI+"/{numParm}").
			then().
				assertThat()
				.statusCode(200)
				.contentType(ContentType.TEXT)
				.body(startsWith(preText)); 
		}
	}
	
	/**
	 * Gets a trivia fact about the TEST_NUMBER as a json response
	 */
	@Test
	public void testGetTriviaForNumber() {
		
		String preText = String.valueOf(TEST_NUMBER)+" is the number of ";
		String numTrivia1 = preText+"furlongs in a mile.";
		String numTrivia2 = preText+"planets in the Solar System.";
		String numTrivia3 = preText+"legs that arachnids have.";
		String numTrivia4 = preText+"principles of Yong in Chinese calligraphy.";
		String numTrivia5 = preText+"bits in a byte.";

		Response jsonRes = given()
		.pathParam("numParm",TEST_NUMBER)			// path parameter
		.param("json",true)							// query parameter
		.when()
		.get(RestAssured.baseURI+"/{numParm}")
		.then()
		.assertThat()
		.statusCode(200)
		.contentType(ContentType.JSON)
		.extract()
		.response();
		
		// Rest Assured, provides a mechanism to reach the values in the API using "path"
		assertThat(jsonRes.path("found"), is(true));
		assertThat(jsonRes.path("type"), containsString("trivia"));
		assertThat(jsonRes.path("number"), equalTo(TEST_NUMBER));
		assertThat(jsonRes.path("text"), containsString(preText));
		assertThat(jsonRes.path("text"), anyOf(is(numTrivia1), is(numTrivia2),
									is(numTrivia3),is(numTrivia4),is(numTrivia5)));
	}
		
	/**
	 * Get a mathematical property about the TEST_NUMBER as plain text
	 */
	@Test
	public void testGetMathsFactForNumber() {
			
		String preText = String.valueOf(TEST_NUMBER)+" is ";
		String mathTrivia1 = preText+"a composite number, its proper divisors being 1, 2, and 4.";
		String mathTrivia2 = preText+"the order of the smallest non-abelian group all of whose subgroups are normal.";
		String mathTrivia3 = preText+"all powers of 2Â ;(2x), have an aliquot sum of one less than themselves.";
		String mathTrivia4 = preText+"the largest cube in the Fibonacci sequence.";
		String mathTrivia5 = preText+"the dimension of the octonions and is the highest possible dimension of a normed division algebra.";
		String mathTrivia6 = preText+"the first number to be the aliquot sum of two numbers other than itself; the discrete biprime 10, and the square number 49.";
	
		given().
		pathParam("numParm",TEST_NUMBER).
		when().
			get(RestAssured.baseURI+"/{numParm}/math").
		then().
			assertThat()
			.statusCode(200)
			.contentType(ContentType.TEXT)
	        .body(anyOf(is(mathTrivia1),is(mathTrivia2),is(mathTrivia3)
	        					,is(mathTrivia4),is(mathTrivia5),is(mathTrivia6)));
	}
	
	/**
	 * Get a fact about a day of year
	 */
	@Test
	public void testGetDayOfYearFact() {
			
		String preText = "August "+String.valueOf(TEST_NUMBER)+"th is the day in ";
			
		given().
		pathParam("numParm",TEST_NUMBER).
		when().
			get(RestAssured.baseURI+"/{numParm}/{numParm}/date").
		then().
			assertThat()
			.statusCode(200)
			.and()
			.contentType(ContentType.TEXT)
			.and()
	        .header("Server",equalTo("nginx/1.4.6 (Ubuntu)"))
	        .header("X-Powered-By",equalTo("Express"))
			.body(startsWith(preText)); 
	}
	
	/**
	 * Get a fact about a year
	 * Return the fact and associated meta-data as a JSON object, with the properties:
	 * 	text: A string of the fact text itself.
	 *  found: Boolean of whether there was a fact for the requested number.
	 *  number: The floating-point number that the fact pertains to.
	 *  type: String of the category of the returned fact.
	 *  date: A day of year associated with some year facts, as a string.
	 */
	@Test
	public void testGetJsonYearFact() {
			
		String preText = TEST_YEAR+" is the year that ";

		Response jsonRes = given().
		pathParam("numParm",TEST_YEAR).
		when().
			get(RestAssured.baseURI+"/{numParm}/year?json").
		then().
			assertThat()
			.statusCode(200)
			.and()
			.contentType(ContentType.JSON)
			.and()
	        .header("Server",equalTo("nginx/1.4.6 (Ubuntu)"))
	        .header("X-Powered-By",equalTo("Express"))
	        .extract().response();
		
		// Rest Assured, provides a mechanism to reach the values in the API using "path"
		assertThat(jsonRes.path("found"), is(true));
		assertThat(jsonRes.path("type"), containsString("year"));
		assertThat(jsonRes.path("number"), equalTo(Integer.parseInt(TEST_YEAR)));
		assertThat(jsonRes.path("text"), containsString(preText));
	}
}



