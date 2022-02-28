# OsuStutterFix
Fix osu! stutters with the click of a button

# Requirements
- Java version 8 or above

# What does this do
🔴 This program will **not** make permanent changes to your system!<br>
🔴 They are only **temporary** and restarting your computer does undo **everything**.

OsuStutterFix sets the affinity of all the currently running user processes to exclude the first CPU core, CPU0.<br>
Since osu! only runs on the first CPU core, removing the ability of other user processes to use CPU0 greatly improves osu!'s common stutter issues.

Doing this will not impact the performance of the changed applications.

In theory, you could do all of this manually by hand via Task Manager but since there are hundreds of processes running at once it would be extremely tedious.

Before             |  After
:-------------------------:|:-------------------------:
![image](https://user-images.githubusercontent.com/52568586/155986118-88ee197f-c291-40c5-b503-89f15ca632a2.png)  |  ![image](https://user-images.githubusercontent.com/52568586/155986269-b108ff1d-1857-4347-8b2b-d522151a62c5.png)

# How to run
- Start osu!
- Run OsuStutterFix.jar
- Wait until it's finished
- Play with improved performance!
