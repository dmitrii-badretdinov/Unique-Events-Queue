# Description

The task is to implement a thread-safe queue with the following properties:

* If we are to add an object which is already in the queue (using equals semantic), nothing should happen. 
* If there is still no such object in the queue (using equals semantic), then the object is added to the end of the queue. 
* Insertion and retrieval times should be O(1).

The resulting class should have the following public methods:

* add(),
* addAll(),
* get().

The additional requirements to the queue are as follows:

* It should be tested and documented.
* It should be efficient in notifying only as many clients as necessary. For instance, if 2 elements are added by addAll(), it should not wake up all 400 clients. If 500 elements are added by addAll(), it should not try to notify 500 clients when there are only 10 of them.
* It should not run out of memory.


# Developer info

The `.gitignore` file was created with consideration of the following:

* The Gradle wrapper should be included. [(proof)](https://stackoverflow.com/questions/20348451/why-should-the-gradle-wrapper-be-committed-to-vcs)
* Some of the `.idea` files should be included when some others should not. [(proof)](https://stackoverflow.com/questions/43198273/which-files-in-idea-folder-should-be-tracked-by-git)