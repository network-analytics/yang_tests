# yang testing

## libyang tests
```
mkdir build
cd build
cmake ..
cmake --build .
ctest -R libyang_full_battery -V
```