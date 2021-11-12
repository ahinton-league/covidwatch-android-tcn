# Health Journey Module
This document outlines the core components within the `health` module

## Dependency Injection

Health Journey uses Koin for dependency injection. It does not yet use the configuration setup (as in Triage) so we do not have any type safety and rely the developer (that's you) to ensure the base apps (ie League, PC Health, Fusion) provide all required dependencies. 

In `healthjourney.java.life.league.healthjourney.injection` you will find the relevant files where dependencies are setup in Koin modules. `HealthJourneyAPIModule` provides API objects for Health Journey which define all of our endpoints. Theses classes depend on the standard API class from league networking module and provided by the core app. `HealthJourneyRepositoryModule` provides Health Journey repositories to interact with the remote APIs. Finally, the `HealthJourneyViewModelModule` provides View Models for all Health Journey views.

Health dependencies that needed to be provided by the core apps include (subject to change):
 1. `API`
 2. `AnalyticsTracker`
 3. `PointsRepository`

These dependencies should be setup in an `ApplicationModule` Koin module in your app.

## API
Health Journey uses a set of `MessageRequest` classes define endpoints and parameters in `HealthProgramsAPI` and `HealthJourneyAPI`. Instances of these classes can be passed to appropriate `sendAndReceiveCachedAndSocketData` or `sendAndReceiveData` extension functions on the core `API` class. These functions will map the object fields to the appropriate JSON format to send as a message to the backend.

## Repository
Health Journey has two repository classes, `HealthJourneyRepository` and `HealthProgramsRepository`
TODO

## View Models
TODO

## Navigation
TODO


## Adding Health Journey to Your App
1. **Add `healthjourney` module as a dependencies to your app so it has access to the components in this module**
	a.  Add `implementation project(":healthjourney")` to the `dependencies` block in your app's `build.gradle` file
2. **Ensure you have provided the dependencies required by the `healthjourney` module's components in `ApplicationModule` (refer to the Dependency Injection section)**
3. **Initialize the Health Journey module by calling `HealthJourney.initialize(...)` with the required dependencies in your app's `Application` class. (refer to LeagueApplication or RoadrunnerApplication for examples)** 
	a. Currently the only dependencies are for this initialization are the `FeatureFlagsUtils` class and 	the `HealthJourneyDrawables` class which takes one drawable resource. The rest of the resources need to be provided in your app's `style` file (next step)
4. **Override required drawables in your app's `styles.xml` file**
		a. Check `drawables.xml` in `genesis-android`for all drawables that need overriding. Currently the Health Journey drawables that need overriding are the following
	```xml
		<attr name="drawable_start_program" format="reference" />  
		<attr name="drawable_health_goal_complete" format="reference" />  
		<attr name="drawable_health_program_complete" format="reference" />  
		<attr name="drawable_health_need_error" format="reference" />

		<attr name="drawable_health_journey_item_removed" format="reference" />  
		<attr name="drawable_health_journey_item_complete" format="reference" />  
		<attr name="drawable_health_journey_empty_activities" format="reference" />  
		<attr name="drawable_health_journey_preview" format="reference" />  
		<attr name="drawable_health_journey_exit_activity" format="reference" />  
		<attr name="drawable_health_programs_not_enrolled" format="reference" />  
		<attr name="drawable_health_programs_enrollment_limit" format="reference" />  
		<attr name="drawable_health_programs_enrollment_available" format="reference" />
	```
	b. Check `styles.xml` in `roadrunner` or `app` for examples of overriding the drawable attributes.
5. **Add the `health_journey_nav_graph` to the main nav graph of your application (refer to League/PC Health's `main_nav_graph` for examples). This will expose all health journey destinations and deep links to the app's `NavController`**
	a. To add health journey to the bottom navigation add the following to app's bottom navigation `menu`   file (e.g. `main_bottom_nav.xml` in PC Health)
	```xml
		<item  
		    android:id="@+id/health_nav_graph"  
    		android:icon="@drawable/ic_tab_journey"  
		    android:title="@string/journey" />
	```
	Replace `@drawable/ic_tab_journey` and `@string/journey` with the appropriate resources for 
	b. Additionally, in order to support deep linking to the progress tab on the health journey page we need some custom handling in the app's `DeepLinker`. Specifically, the following case needs to be included in the `navigateToDeepLink` function in the `DeepLinker` 
	```kotlin
	 path matches HealthJourneyDeepLinker.HealthJourneyPaths.HealthProgramsProgress.path ->
	     HealthJourneyDeepLinker.HealthJourneyPaths.HealthJourney.construct(
	         HealthJourneyDeepLinker.HealthJourneyPaths.HealthJourney.HealthJourneyTab.Progress
	     )
	```
