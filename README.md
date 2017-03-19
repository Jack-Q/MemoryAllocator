Memory Allocator
================

Modules
-------

* `core`
  
  A pure java program implementes a simplified version of some memory 
  allocation methods for demostration purpose. 

* `gui`

  A GUI program to illustrate the allocation algorithms with JavaFx framework.
  This GUI provide an interface to pass the control command to the core 
  module and 

Build & Run
-----------

* Prerequisites
  + JDK 1.8 (with JavaFx)
  + Gradle 2/3

* Build
  ```bash
  gradle build
  ```
* Run
  ```bash
  # execute the core module (a command line program)
  gradle xjtu.thinkerandperformer.memoryallocator.core:run -q

  # execute the GUI version of the program (JavaFx)
  gradle xjtu.thinkerandperformer.memoryallocator.gui:run -q
  ```