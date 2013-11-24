lorenz
======

Visual illustration of the essential nature of chaos using the Lorenz butterfly
attractor

Project organization
--------------------

This is organized as a sequence of sub-projects, a/k/a "labs", representing
stages in the development of the final app. Each lab builds on the previous
ones, but is fully self-contained. All the code for a given lab resides in a
source directory labeled with the lab's number. The finished project resides
in its own source directory, named "final". See "Outline of the Labs" below
for a summary of what each lab covers. 

Technical information
---------------------

The executable app is called "LorenzLab", and is an ordinary Java application.

This project is both a git repository and an Eclipse Plug-In Project.
I have kept dependencies to a minimum.


Other relevant things of note:

* Java SE 1.6
* GUI library is SWT.

   

Outline of the Labs
-------------------

1. display an SWT window with a sample message in it. No content yet.

2. display data points that are generated programmatically. This requires transforming
from 3-D double-valued data coordinates to the 2-D integer-valued coordinates used by
the canvas. 

3. Add live animation by having the app spin off a deamon thread on which the
   data points are produced. (It will help to make the point-generator interface
   extend Runnable.) Add buttons to the GUI to start and stop the production of
   new data points. Have it keep generating new points so long as it's running.

4. Define event-style APIs for  a thing that generates data points
   (PointGenerator) and a thing that receives them and does something with them
   (PointListener). Have a look at java.util.Event and java.util.EventListener
   for inspiration. The PointGenerator interface should have methods for adding
   and removing PointListeners.

5. Make it so that you can set up the Graph to receive data points from multiple
   PointGenerators and display them in different colors. Also, have it keep the
   points to be displayed in a queue of bounded length, so that it will only
   display the most-recent N points it's received.

6. write a couple warmup point-generators: (1) one that does a random walk in
   the unit cube. (2) one that generates Lissajous figures. Add GUI stuff to let
   user choose which point-generator he wants to use

7. write a point-generator for the Lorenz system and hook it in as a possible
   point-generator.

8. Set it up to show off differences between two trajectories that start out
   imperceptibly near each other. Do this by having it display two trajectories
   (in two colors) that differ ONLY in that they start from two imperceptibly
   different points. Make it so that you can pick which point-generator to use
   with it.  In RandomWalk, the two trajectories go their separate ways
   immediately. In Lissajous the trajectories will remain on top of each other
   forever. In Lorenz, the trajectories will start out on top of each other but
   eventually will separate to trace out the 'butterfly' shape.
   
