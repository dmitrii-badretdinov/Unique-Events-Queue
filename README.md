# Project Description

We are the aggregator of events that our clients consume. One event per client. Each client is a thread.  
To aggregate the events, we settled on a thread-safe queue with the following properties:

* If we are to add an object which is already in the queue, nothing should happen. 
* If there is still no such object in the queue, then the object is added to the end of the queue. 
* Insertion and retrieval times should be O(1).
* The queue should be efficient in notifying only as many clients as necessary. For instance, if the batch of two elements is added, it should not wake up all 400 clients. If the batch of 500 elements are added, it should not try to notify 500 clients when there are only 10 of them.

The resulting class should have the following public methods:

* add(),
* addAll(),
* get().

# Developer info

The `.gitignore` file was created with consideration of the following:

* The Gradle wrapper should be included. [(proof)](https://stackoverflow.com/questions/20348451/why-should-the-gradle-wrapper-be-committed-to-vcs)
* Some of the `.idea` files should be included when some others should not. [(proof)](https://stackoverflow.com/questions/43198273/which-files-in-idea-folder-should-be-tracked-by-git)
