lorenz
======

A visual illustration of the essential nature of chaos using the Lorenz butterfly
attractor. For extra fun, it is organized as a series of self-contained "labs"
-- a/k/a/ sub-projects or "sprints" -- that showcase the incremental development
of the final app.

Each lab builds on the previous ones, but is fully self-contained. All the code
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
and stop the production of new data.

5. Lorenz system

Introduce the Lorenz equations and numerical integration.

6. Multiple timeseries

Add support for multiple independent timeseries. Show the Lorenz system's
sensitive dependence on initial conditions.

7. Multiple scenarios

Add support for multiple "scenarios", each of which has its own data sources and
configuration parameters; add GUI for scenario selection. 

8. Controls

Adds GUI controls for setting the scenarios' parameters.

9. More scenarios

Adds new types of data source, and a scenario for each.

--------------------------------------

(The summary of Labs ends here. Remnants of an old schedule follow.)

--all the data-gen's operate over same timescale and spatial region

--axes?

--Stable ODE such as pendulum

--GUI support to choose the data source and its config params
  
--Quick menu w/ prefab configs that show interesting features.

--(maybe) Better data transform to provide more 3-d look

--smoother animation, somehow. Maybe drive repaint requests off a timer?

--save data to file(s) as well as displaying it. Use DataSourceListener(s).

--Load config data from file. Generate config files for three cases:
  Lorenz, random, pendulum; 2 timeseries for each w/ tiny offset in IC.
   
