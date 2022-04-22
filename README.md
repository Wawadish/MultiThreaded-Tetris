# MultiThreaded-Tetris

Each tetromino is handled by a thread, threads must synchronize to ensure correctness of the program. 
They must acquire and release locks to move and occupy a tetris cell.

./q1 n k p d m
n = (int) grid height     > 10  </br>
k = (int) new tetromino periodicity     >=2</br>
p = (double) probability tetromino transformation </br>
d = (int) max downards movement       >=1</br>
m = (int) total number of tetrominos spawned    >=1</br>
