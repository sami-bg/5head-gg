# cs0320 Term Project 2020

**Team Members:**
* Salvador Brandi
* Sami Bou Ghanem
* Miguel Siordia
* Walter Zhang

**Team Strengths and Weaknesses:**
* Salvador: 
  Strengths: Operational knowledge of many programing languages. SQL 
  Weakness: Poor commenting habits, can't make tests well
* Sami:
  Strengths: Good at CSS, front-end design. Good at unit and system tests.
  Weaknesses: Can't SQL for my life. 
* Miguel:
  Strengths: Pretty good at debugging, good with design (not CSS though)
  Weaknesses: Pretty bad at CSS
* Walter:
  Strengths: Good at implementing algorithms, testing
  Weaknesses: Not great at abstracting classes / making things extensible


**Project Idea(s):**
### Idea 1 
Given a list of tasks, time constraints, and priority weight, automatically fill out the times they should be working on which task, and export that to GCal. Dynamically changes when adding more tasks/meetings to optimally fill time. 
Issue tracker.

TA review: Rejected - this idea seems like it's simply going to find overlapping time intervals, which is not complex enough for this assignment. 


### Idea 2
A software that can look at a corpus of memes, and then generates a set of relevant keywords that apply to each meme by looking at their content and comment section. You can then sift through the memes via a certain hashtag (or multiple), and find memes that are relevant to any and all meme pages.

TA review: Rejected - this idea will be heavily based on categorizing the memes (and then will just be a filter in the end), and the categorization algorithm seems like it belongs in a different CS class, not 32.


### Idea 3
Use the user’s spotify song history to identify the emotions most commonly associated with them. Create a histogram/map of how the users song-emotions change over time / per playlist, etc. 

TA review: Rejected - there is not enough algorithmic complexity behind this idea.

**Note:** You must push a revised idea to Github.

# REVISED IDEA

# Online Game Winrate Prediction:

Online games are being changed all the time, with the changes being released to the public before they actually are applied to the game. This means that for any given patch, there is a period in which people speculate about how the changes will affect how the game is played, and which strategies will be most effective. Since these games are complex systems, most changes impact the meta (most effective technique available) in non-obvious ways. People often have wildly different opinions based on their understanding of a game. We propose a web application to allow users to place value on their predictions, gaining rewards for making accurate predictions. There are many games which this could be applied to, including names like Overwatch, League of Legends, Dota 2, and Hearthstone. In the last decade, esports analysis has become a real career. Through this interface,  esports analysts and casual gamers alike, could prove their understanding of the game as a whole to the public, lending credibility to their analysis. 

## Requirements
* Web scraping for patch notes, and conversion of patch notes into quantifiable attributes
* An algorithm that places weights on differents aspects of the patch notes to normalize how impactful different types of buffs are. 
* Backend to store user profiles, Main.Patch data, trends, etc
* Use of company APIs to collect game state data
* Front end development to display Users, interface, games, change categories, etc. 

## Features 
* Users can create an account and accrue reputation on their profile page
* Correctly predicting the variation that a change will cause in game state awards reputation, while incorrectly predicting it causes loss of reputation
* Different interfaces and profiles for League of Legends and Rainbow Six Siege
* Analytics data trends and graphs for each prediction category


## Challenges
* Frontend will require significant development to convey information clearly
* Developing a fair weighting algorithm may require extensive user testing/feedback which is difficult to collect. 
* Making the code extensible to easily be able to build on the framework in the future. 

**TA Review: ** Approved! Looks good, make sure to discuss your algorithm with your mentor TA or Tim before coding it. 

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 13)_

**4-Way Checkpoint:** _(Schedule for on or before April 23)_

**Adversary Checkpoint:** _(Schedule for on or before April 29 once you are assigned an adversary TA)_

## How to Build and Run
Build the package by running `mvn package`.

Run the program by running the `run` executable. This will start a spark server listening on 0.0.0.0:4567.
navigating to that in a web browser on the computer you ran it on will allow you to access the site. 

## Testing

The project has been thoroughly JUnit tested, with ~90% function coverage for each module. The Main class has low testing coverage, since many of its functions are for displaying the frontend, and can't be easily tested. 

In addition, we hand tested the front end. We used W3C's HTML and CSS validators to ensure our frontend code was good. A record of the hand tests we performed on the frontend can be found in test/HandTests.md
