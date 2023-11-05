## Further Language Constructs
### Task 1: Introduction


The aim of this workbook is to introduce further concepts and constructs from the Java programming language.
In order to support this, we will continue working with the "Shapes" projects from last two weeks,
which you will extend and enhance with additional features and functionality.
As before, each section in this workbook links to some fragments of teaching materials that introduces a concept,
as well as an activity to undertake that will allow you to apply your knowledge. 
  


# 
### Task 2: Abstract Classes
 <a href='02%20Abstract%20Classes/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='02%20Abstract%20Classes/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

An inspection of the code inside the `TwoDimensionalShape` class (found in `src/main/java/` folder of the Maven project)
will reveal that the class has two **abstract** methods.
These are methods _without_ implementations - they have signatures ("prototypes"), but no internal code.
View the slides and video lecture above to find out more about the notion of **abstract** and the reasons why we might want to use this feature of Java.

Now, inside your `Triangle` class, write complete `calculateArea` and `calculatePerimeterLength` methods so that each performs a suitable
calculation and returns an appropriate result.
These two methods will **override** the abstract (blank) methods defined in the `TwoDimensionalShape` class.

The reason why these two methods were originally defined as **abstract** in the `TwoDimensionalShape` class is as follows...
All shapes _will_ have an area and perimeter length, however: these will be _calculated_ differently depending on the shape being represented.
(Circles this is done using their radius and Pi, with Rectangles this is down using their width and height etc.)
As such, we can't write anything _generally useful_ inside the `TwoDimensionalShape` class - we have to leave
everything down to the subclasses themselves. That said, it is still worth _defining_ that all shapes have area and perimeter calculations
(even if they have to be left blank in the `TwoDimensionalShape` class).

Once you have implemented area and perimeter calculation methods for your `Triangle` class, create a few
`Triangles` with different length sides and call your freshly created methods to make sure they work correctly.
  


**Hints & Tips:**  
Calculating the perimeter length of a triangle is easy.  
In order to calculate the area you might like to use
<a href="https://www.mathsisfun.com/geometry/herons-formula.html" target="_blank">Herons Formula</a>.  


# 
### Task 3: Interfaces
 <a href='03%20Interfaces/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='03%20Interfaces/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

As we have discussed previously, some shapes have a number of variants,
whilst others (such as Circles) have only one. 
It would be useful to have some kind of "marker" that indicates
whether or not a particular shape has multiple variants.

One way to do this is using a Java **Interface** - this is a mechanism, independent of the class hierarchy,
that allows us to "mark" classes as having particular characteristics. You can think of an Interface as a "contract"
that the class "conforms to" - guaranteeing to provide certain properties and features.
Take a look at the slides and video above for more detailed information about interfaces.

As an illustration of interfaces in action, we have provided a `MultiVariantShape` interface in the Maven
project that can be used to indicate that a shape has more than one variant.
We can mark our `Triangle` class as a shape which has multiple variants by updating the class definition
so that it `implements` the `MultiVariantShape` interface:
``` java
class Triangle extends TwoDimensionalShape implements MultiVariantShape
```
This indicates that the `Triangle` class conforms to the properties and features of the `MultiVariantShape` interface.

Elsewhere in our code, we can test to see if a particular object conforms to an interface by using the `instanceof` keyword
(I know that this is not a particularly suitable name, but it is reused from another part of Java).
We can test any shape object to see if it implements the `MultiVariantShape` interface using the following code:
``` java
if(shape instanceof MultiVariantShape) System.out.println("This shape has multiple variants")
else System.out.println("This shape has only one variant")
```

You may have noticed that the `MultiVariantShape` interface defines a method called `getVariant`
that should return the particular variant of a shape. This allows code outside of the class to interrogate an instance
in order to find out which variant it is. You will also notice that in the interface definition, their is no code that
provides an implementation of this method - it your job as the programmer's to write this !
Luckily you wrote this method in the previous workbook, so it is therefore a quick and an easy job to implement
the `MultiVariantShape` interface at this stage !

  


# 
### Task 4: Arrays
 <a href='04%20Arrays/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='04%20Arrays/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

Common to most programming languages, Java provides an **array** data structure (similar in many ways to that found in C). In Java, arrays can hold primitives (e.g. `int`, `float`, `char`, `boolean` etc)
but ALSO complex Objects (such as `Strings`, `Robots` or even `Triangles`) !

Take a look at the slides and video above for more information on using arrays in Java.
Then (in your main method) create an array of size 100 that can hold `TwoDimensionalShapes`.
Fill this array with randomly chosen shapes (`Circles`, `Rectangles`, `Triangles`).
In order to fill your array with a random selection of shapes, you may like to make use of the
Java `Math.random()` method. This will return a randomly selected double precision floating point
number (between 0.0 and 1.0). You will need to write an IF statement and use some cunning maths
to look at this number and decide which shape to create.

Once you have filled the array with 100 random shapes, loop through it and print out the details of each shape in turn.
At the end of your main method, print out a summary of how many `Triangles` were in the array.
In order to do this, you will need to make use of an integer counter variable to keep a tally of the number of triangles encountered.
You will also need to make use of the `instanceof` operator to check the type of each instance in the array.

  


# 
### Task 5: Class Variables and Methods
 <a href='05%20Class%20Variables%20and%20Methods/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='05%20Class%20Variables%20and%20Methods/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

In the previous task, we counted the number of `Triangles` in the array by looping through it.
But there is an alternative way to achieve the same objective (without putting code inside the main loop).
We could maintain a counter of how many `Triangles` have been created _inside_ the Triangle class
and increment this each time a Triangle is instantiated.
This would encapsulate the "population" count inside the class itself - which is nice.

But there is a problem this with approach - we only want _one_ counter for the whole program
(not a counter inside _each and every_ `Triangle` instance). For this reason, we can't just use a "normal" variable.
View the slides and audio narration above to gain an understanding of the difference
between **instance** and **class** variables. Then add an integer **class** variable to your `Triangle` class to
keep track of how many `Triangles` have been created. Once you have declared with variable, you can update your
`Triangle` constructor method so that the counter is incremented each time a new `Triangle` is created.
You should also add a `getPopulation` method to your `Triangle` class that returns the current value of this
counter variable, so that it can be accessed from outside the class.

Update your main method, so that it prints out the number of `Triangles` that exist using
this class variable population counter _as well as_ the previous counter from inside the loop
(just to make sure both counters agree !)

Just as we can create **class variables** that are associated with the class (rather than each instance),
we can also write **class methods** which are similarly associated with the class (rather than the instances).
You have already encountered just such a class method (namely the `random` method of the `Math` class).
Have a think about the reasons why you might wish to write a class method, rather than an instance method.
Considering the `Triangle` class - might some of its methods be better suited to being class methods,
rather than instance methods ?
  


# 
### Task 6: Casting


Let's imagine that we wanted to find out the variant of all `Triangles` that are stored in our array.
We would need to be very careful about how we went about interrogating the elements of that array.
We can't call `getVariant` on just any element, since not all objects will actually _be_ a `Triangle`
(it might be a `Circle` for example, which doesn't support the `getVariant` method !)
Java will in fact block us from trying to call `getVariant` on a `TwoDimentionalShape`
(try it and see !).

To solve this problem, Java allows us **cast** objects from one class into a _more specific class_.
For example, we can "down-cast" (or "narrow-cast") any `TwoDimentionalShape` into one of its sub-classes.
We can then quite happily call methods of that subclass on the instance.
For example, in the code fragment below we extract the first element from the array,
convert it into a `Triangle` and then calle the `getVariant` method on it:

``` java
TwoDimentionalShape[] shapes = new TwoDimentionalShape[100];
...
TwoDimentionalShape firstShape = shapes[0];
// Down-cast the shape into a triangle
Triangle firstTriangle = (Triangle)firstShape;
TriangleVariant variant = firstTriangle.getVariant();
```

The down-casting code above asks Java to "trust" that the `firstShape` is a `Triangle`.
As a consequence of this, we can then treat this object as though it were a `Triangle` and thus call
any method of the `Triangle` class on it.

This is fine, _provided_ that the shape at element 0 of the array _is actually_ a `Triangle`.  
But what happens if it _isn't_ ?  
What if it is a `Circle` instead ?  
Will Java let us convert a `Circle` into a `Triangle` in this way ?

Why not try it and see what happens !
  


# 
### Task 7: Multiple Inheritance
 <a href='07%20Multiple%20Inheritance/slides/segment-1.pdf' target='_blank'> ![](../../resources/icons/slides.png) </a> <a href='07%20Multiple%20Inheritance/video/segment-1.mp4' target='_blank'> ![](../../resources/icons/video.png) </a>

As a final topic for this workbook, let us think about the challenging concept of _multiple_ inheritance.
In some situations it might be useful for a class to have _multiple_ parents/superclasses.
This would allow it to derive methods and attributes from numerous different sources.
This is however dangerous - take a look at the slides and video above to find out why !

Java's solution to this is to only allow single inheritance :o(  
But to augment this with **interfaces** :o)  

Although a bit of a compromise, this mechanism allows us to gain _some_ of the benefits of multiple inheritance, whilst avoiding many of the main pitfalls.  


# 
