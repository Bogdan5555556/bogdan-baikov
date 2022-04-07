Charters
------------------------------------
#####Charter 1: Explore a diagram on main screen

#####Coverage
**Areas:**
1) Diagram on a main screen with all interactive (categories) buttons around
2) Balance hidden menu
3) Big buttons at bottom of the screen
4) Hidden menu of adding an expense

#####Strategy

Various inputs for incomes and outcomes with various categories 

#####Task Breakdown:
_Duration_
-50 min

_Test design and execution_
-20 min

_Bugs describing_
-20 min

_Session setup_
-5 min

#####Test Notes
 - Add various expenses through the active categories buttons around pie chart
 - Add various expenses through the active with buttons under pie chart
 - Change created income/outcome through the balance hidden menu
 
#####Bugs
1) [Functional] If outcome is to small 
    - it is not showed at pie chart any how
    - there is no connection line between pie chart and category button
    - expense is showed with 0% 
2) [Functional] Long pressing on category button should not open hidden menu, but should 
only show aggregated outcome for chosen category 
3) [Functional] Pressing on pie chart should not open hidden balance menu,
 and should only highlight a chosen category and show aggregated outcome for chosen category 
4) [Usability] Pressing on balance menu under pie chart should open hidden menu with list of expenses
with focus on latest expenses, but focus on where it was left last time  
5) [Usability] To hide opened balance menu need to push button at the top of the screen - too hard to press with one hand 
6) Screen of adding new expense inside predefined category looks like simple calculator 
    - [Usability] field with amount of spent money is not clickable - not possible to change (delete) particular digit 
    - [Usability] you don't see already applied arithmetic operations
    - [Usability] you cannot paste your expense from buffer 
7) Screen of adding new expense inside big red (green) buttons  
    - [Usability] you cannot choose category before amount insert
    - [Usability] after push to choosing category button you cannot go back to 'calculator' screen
8) [Usability] At the screen with money transfer between your accounts you can save transfer only at the calculator view
 
 
 #####Charter 2: Explore settings hidden menu on the left side of the screen
 
 #####Coverage
 **Areas:**
 1) Hidden menu on the left side of the screen
 2) All inner views 
 
 #####Strategy
 
 Various inputs for incomes and outcomes with various categories 
 
 #####Task Breakdown:
 _Duration_
 -25 min
 
 _Test design and execution_
 -15 min
 
 _Bugs describing_
 -10 min
 
 
 #####Test Notes
  - Try various settings and special functions
  
 #####Bugs
 1) [Functional] Export to file is not working properly - file was not created and sent to receiver 3 times out of 10
 2) [Usability] In budget mode you see only defined budget and don't see amount of incomes
 3) [Functional] Changing of week start day (or month) is not updating on the fly 
 4) [Usability] Copy of expense ID - is not clear what should copy  