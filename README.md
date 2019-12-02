# Bachelor's Thesis
A Practical Application of Logo Recognition

This repository contains the source code and documentation of the final year project thesis for my bachelor’s degree in Computer Science and Business from the University of Dublin, Trinity College.


# Outline
(For the full project report see: [Report](https://github.com/lowrydonal/bachelors-thesis/blob/master/Report.pdf))

The purpose of the program is to check whether a certain logo is present in an inputted image. 

The implementation uses a number of pre-processing techniques to first attempt to pinpoint the location a logo in an input image.

![alt text](https://github.com/lowrydonal/bachelors-thesis/blob/master/Java%20Project/markdown/img2.png )

The extracted logo sub-image is then compared to a stored template of the logo in question to check if they match. This comparison is done using the SIFT (Scale Invariant Feature Transform) algorithm.

![alt text](https://github.com/lowrydonal/bachelors-thesis/blob/master/Java%20Project/markdown/img1.png )

Statistical methods were then employed to test the accuracy of the logo recognition.
