
cmake_minimum_required(VERSION 3.10)
project(libyang_full_battery C)

set(CMAKE_C_STANDARD 11)

find_package(PkgConfig REQUIRED)
pkg_check_modules(LIBYANG REQUIRED libyang)
pkg_check_modules(CMOCKA REQUIRED cmocka)

include_directories(
    ${LIBYANG_INCLUDE_DIRS}
    ${CMOCKA_INCLUDE_DIRS}
    src
)

link_directories(
    ${LIBYANG_LIBRARY_DIRS}
    ${CMOCKA_LIBRARY_DIRS}
)

add_library(utils src/utils.c)
target_link_libraries(utils ${LIBYANG_LIBRARIES})

add_executable(test_full_battery tests/test_full_battery.c)
target_link_libraries(test_full_battery utils ${LIBYANG_LIBRARIES} ${CMOCKA_LIBRARIES})

enable_testing()
add_test(NAME libyang_full_battery COMMAND test_full_battery)
