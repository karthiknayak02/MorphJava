# MazeGeneratorSolverJava
Java stand-alone application that provide a user interface and backend driver that supports the rendering of a piecewise (triangular) image morph between two images. 

![Grid](/MorphWillFerrell.gif)

Rendered morph warps and cross-dissolves between the two images. 
User is able to read in the desired images 
# Features
`Image Enhancement`	: Implements the ability to apply a simple intensity (brightness) adjustment to the start and end images.
`Grid Resolution`	: Implements the ability to change the grid resolution. Three grid resolution settings (5x5, 10x10, 20x20).

## Usage
1. Load Images with the `File` menu button at the top left. 							<br/>
2. To adjust the brightness of each image before the rendering, use the `<` `>` buttons under each image.	<br/>
3. Drag control points around and match similar features on each image. 					<br/>
4. Change grid size by using the `Grid Size` menu item. 							<br/>
5. After each image is loaded and points manipulated, click `Preview` to view the morph.			<br/>
6. To export each frame as a .jpeg image, then check the `export images` checkbox.				<br/>
