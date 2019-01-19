#! /usr/bin/env python

# The aim of this python script is doing a report
# of the codereview that we have to do

# Organizing of the environment: array of stundents

# Index of
actualIdIndex = 0
firstIdIndex  = 1
surnameIndex  = 2
groupIndex    = 3

# Array of students
# [Actual id, first id, surname, group]
students = [
    [ 0,  0, "Fasan",     1],
    [ 1,  1, "Garrido",   1],
    [ 2,  2, "Molinaro",  1],
    [-1,  3, "Piva",      1],
    [ 3,  4, "Porro",     1],
    [ 4,  5, "Pratesi",   1],
    [ 5,  6, "Bullaku",   2],
    [ 6,  7, "Furlan",    2],
    [ 7,  8, "Prandini",  2],
    [ 8,  9, "Rossi",     2],
    [ 9, 10, "Valente",   2],
    [10, 11, "Balzan",    3],
    [11, 12, "Cervo",     3],
    [12, 13, "Moroldo",   3],
    [13, 14, "Pham",      3],
    [14, 15, "Romanello", 3],
    [15, 16, "Bedin",     4],
    [-1, 17, "Iosif",     4],
    [16, 18, "Perali",    4],
    [17, 19, "Ton",       4],
    [-1, 20, "Vicentini", 4]
]

# Returns the actual students number
def countActualStudentsNumber(students):
    counter = len(students)
    for student in range(0,len(students)):
        if (students[student][actualIdIndex] == -1):
            counter -= 1
    return counter

firstStudentsNumber = len(students)
actualStudentsNumber = countActualStudentsNumber(students)

# These variables are useful for calculating the Mod
reviewFromA = -7
reviewFromB = -13

reviewToA = -reviewFromA
reviewToB = -reviewFromB

firstReviewFromA = +7
firstReviewFromB = +13

firstReviewToA = -firstReviewFromA
firstReviewToB = -firstReviewFromB

# Calcuates the mod
def calculateMod(user,index,total):
    return ((user+index)%total)

# Returns the student that correspond to the first id
def studentFromFirstId(studentFirstId,students):
    for id in range(0, len(students)):
        if students[id][firstIdIndex] == studentFirstId:
            return students[id]

# Starts writing report
report = open('codereview.txt', 'w')

# Title
report.write("CODEREVIEW LIST\n")
report.write("---\n")
report.write("StudentsNumber " + str(actualStudentsNumber)
    + " - ID(0," + str(actualStudentsNumber - 1)+ ")\n")
report.write("(my_id + (" + str(reviewFromA) + "))%"
    + str(actualStudentsNumber) + "\n")
report.write("(my_id + (" + str(reviewFromB) + "))%"
    + str(actualStudentsNumber) + "\n")
report.write("---\n")

# Students details
for id in range(0, len(students)):
    name = students[id][surnameIndex]
    group = students[id][groupIndex]
    actualId = students[id][actualIdIndex]
    firstId = students[id][firstIdIndex]

    # Student title
    report.write("\n--> Student " + str(name) + " (g"+ str(group) +") _ id "
        + str(actualId) + " (first id " + str(firstId) + ")\n")

    if actualId == -1:
        report.write("Not in course anymore\n")
        report.write("---\n")
    else:
        # Calculates the assignments for this session
        fromA = studentFromFirstId(
            calculateMod(firstId,reviewFromA,firstStudentsNumber),students)
        fromB = studentFromFirstId(
            calculateMod(firstId,reviewFromB,firstStudentsNumber),students)
        toA = studentFromFirstId(
            calculateMod(firstId,reviewToA,firstStudentsNumber),students)
        toB = studentFromFirstId(
            calculateMod(firstId,reviewToB,firstStudentsNumber),students)

        # Calculates the assignments of the last session
        firstFromA = studentFromFirstId(
            calculateMod(firstId,firstReviewFromA,firstStudentsNumber),students)
        firstFromB = studentFromFirstId(
            calculateMod(firstId,firstReviewFromB,firstStudentsNumber),students)
        firstToA = studentFromFirstId(
            calculateMod(firstId,firstReviewToA,firstStudentsNumber),students)
        firstToB = studentFromFirstId(
            calculateMod(firstId,firstReviewToB,firstStudentsNumber),students)

        # Writes the results
        report.write("Codereview of " + str(students[id][actualIdIndex])
            + " from "
            + str(fromA[actualIdIndex]) + " (" + fromA[surnameIndex] + ")\n")
        report.write("Codereview of " + str(students[id][actualIdIndex])
            + " from "
            + str(fromB[actualIdIndex]) + " (" + fromB[surnameIndex] + ")\n")
        report.write("(last time from " + str(firstFromA[actualIdIndex]) + " ("
            + firstFromA[surnameIndex] + ") and "
            + str(firstFromB[actualIdIndex])
            + " (" + firstFromB[surnameIndex] + "))\n")
        report.write("\n")
        report.write("Codereview to " + str(toA[actualIdIndex])
            + " (" + toA[surnameIndex] + ") and "
            + str(toB[actualIdIndex]) + " ("+ toB[surnameIndex] + ")\n")
        report.write("(last time to " + str(firstToA[actualIdIndex])
            + " ("+ firstToA[surnameIndex] + ") and "
            + str(firstToB[actualIdIndex]) + " ("+ firstToB[surnameIndex]
            + "))\n")
        report.write("---\n")

# Closes the report
report.close()
