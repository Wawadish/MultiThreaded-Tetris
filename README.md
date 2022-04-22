# MultiThreaded-Tetris

Each tetromino is handled by a thread, threads must synchronize to ensure correctness of the program. 
They must acquire and release locks to move and occupy a tetris cell.

./q1 int_grid_height int_new_tetromino_periodicity double_probability_tetromino_transformation int_max_downards_movement int_total_number_tetrominos_spawned
