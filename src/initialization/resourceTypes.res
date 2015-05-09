dailyAvailability :
  - startTime : "12:00"
    endTime   : "17:00"

resourceTypes:
  - name              : "car"
    # Inner lists are between "[" and "]". Their items are seperated by ",".
    requires          : []
    conflictsWith     : []
    dailyAvailability :
  - name              : "white board"
    requires          : []
    conflictsWith     : []
    dailyAvailability :
  - name              : "demo kit"
    requires          : []
    conflictsWith     : [1]
    dailyAvailability :
  - name              : "conference room"
    requires          : [2]
    conflictsWith     : [3]
    dailyAvailability :
  - name              : "distributed testing setup"
    requires          : []
    conflictsWith     : []
    dailyAvailability :
  - name              : "data center"
    requires          : []
    conflictsWith     : []
    dailyAvailability : 0