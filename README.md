# Fruit-Machine-Simulator-with-Excel-Interface [![Build Status](https://travis-ci.org/VelbazhdSoftwareLLC/Fruit-Machine-Simulator-with-Excel-Interface.svg?branch=master)](https://travis-ci.org/VelbazhdSoftwareLLC/Fruit-Machine-Simulator-with-Excel-Interface) 

Fruit machine simulator with Excel input-output interface.

Modern slot machines are the most popular form of gambling games. These games inherit the mechanical slot machines where reels were disks with symbols drawn on the surface. The electronic slot machines do not have mechanical reels. Instead, they have virtual reels. Game symbols are organised in two-dimensional arrays where each column presents a single reel. The player spins the reels and after a short period of time, they stop. Stopped reels do form the game spin outcome. Wins are counted from left to right usually according to predefined winning lines. Some games have wild symbols that substitute any other symbol without scatters. Scatter wins are not organised in lines and they are formed by scatter symbols that can appear on any place on the screen.

Slot machines, like any other gambling game, have a common parameter called a return to player (RTP). The RTP is the percentage of the bet which the player will get back statistically in a single run. For example, in a game with 95% RTP, it means that if you bet 100 dollars in a statistical manner you will get back 95 dollars after a single game run. The RTP parameter is very important and it is the most strictly controlled characteristic of any gambling game around the world by the gambling commissions. The other interesting parameter is the game the volatility, but it attracts much less attention from the gamblers and government regulators. The volatility is about how often the game will give you small wins and big wins. If the game gives small wins, but often it is a low volatility game. Otherwise, if the game gives high wins, but rarely it is a high volatility game. 

When you are creating a slot machine or you are checking slot machine created by somebody else you need to make a combinatorial calculation of the RTP or to do a Monte-Carlo simulation. Some slot machines are so complex that even the best mathematicians in the world are not capable to make a combinatorial computation of the RTP. In such cases, a Monte-Carlo simulation is the only choice. This project is about a Monte-Carlo simulator that is capable to calculate many statistical parameters of a slot machine mathematical model. 

## Building Commands

gradle wrapper
./gradlew jarAll
