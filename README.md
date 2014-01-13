lorenz
======

This project has two different objectives: (1) to develop an application that visually
illustrates the essential nature of chaos using the Lorenz butterfly attractor; and
(2) to create a series of self-contained "labs" (some might say sub-projects or
"sprints") that show stages in the incremental development of the final app.

Each lab builds on the previous labs, but is fully self-contained. All the code
for a given lab resides in a source directory labeled with the lab's number. The
finished project resides in its own source directory, named "final".

For all labs, the executable app is called "LorenzLab". It runs as an ordinary
Java application. To run it, by far the easiest option is to launch it from within
Eclipse.

Note: this project as a whole is both a git repository and an Eclipse Plug-In Project.
It was developed with Eclipse 4.3 and JavaSE 1.6.

Outline of the Labs
-------------------

1. Getting Ready

	Display an SWT window showing a canvas. Draw something in the middle of the canvas.

2. Data Points and Transforms

	Define data points (points in a 3-D Euclidean space). Implement a simple transform
to map data points onto pixel locations. Draw some sample data points on the canvas.

3. Data Plumbing

	Separate the production of data from its display.

4. Animation

	Add live animation of a time-sequence of data points. Create GUI controls to start
and stop it.

5. Lorenz system

	Introduce the Lorenz equations and numerical integration.

6. Multiple timeseries

	Add support for multiple independent timeseries. Show the Lorenz system's
sensitive dependence on initial conditions.

7. Multiple scenarios

	Add support for multiple "scenarios," each of which has its own data sources and
configuration parameters; add GUI for scenario selection. 

8. Controls

	Adds GUI controls for setting the scenarios' parameters.

9. More scenarios

	Adds new data source types and a scenario for each.

10. Clean & Polish

	Cleans and polishes what we've got so far.

