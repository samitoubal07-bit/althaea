# Althaea
A personalised meal planner built around your fitness goals.

Just tell it what ingredients you have in your fridge, how active you are, and whether 
you want to cut, maintain or bulk and it calculates your daily macro targets 
and suggests real recipes matched to your goals via the Spoonacular API.

Recipes are shown as cards you can swipe on: swipe right to add to your plan, 
swipe left to skip. Accepted meals form your daily meal plan with 
full macro breakdowns.

## Tech stack
Backend    Java 21, Spring Boot 4, Spring Security, Maven
Database   PostgreSQL, Spring Data JPA, Hibernate
Auth       JWT (jjwt 0.12) 
Frontend   React, Framer Motion, Axios
Recipes    Spoonacular API

## How the goal system works

Users select two axes that drive all macro calculations:

Body goal - Cut (−20% calories), MAINTAIN, BULK (+15% calories)  
Fitness goal - Fat loss, endurance, muscle gain

These combine to make 9 possible profiles, each with a different macro split.

## Running locally

### Prerequisites
- Java 21
- Maven
- PostgreSQL
- Node.js

### Backend
1. Create a PostgreSQL database called `althaea`
2. Add your Spoonacular API key to `application.properties`
3. Run `mvn spring-boot:run`

### Frontend
1. `cd althaea-frontend`
2. `npm install`
3. `npm start`

## What I'm going to add next
- Workout logging that adjusts same-day macro targets
- Persistent 7-day plan view
- Smarter recipe filtering by macro targets
- Higher Spoonacular API tier for more suggestions
- Recipe Instructions and ability to find out more about the recipe by clicking.
