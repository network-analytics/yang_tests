# yang testing

## libyang tests
```
mkdir build
cd build
cmake ../libyang
cmake --build .
ctest -R libyang_full_battery -V
```

## Prerequisites for Java-based tests

This project requires Java 21+ and Maven 3.6.3+

### Java Installation (version 21 as example)


```
# Download Java 21
cd /tmp
wget https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.5%2B11/OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz

# Extract
sudo mkdir -p /opt/java
sudo tar -xzf OpenJDK21U-jdk_x64_linux_hotspot_21.0.5_11.tar.gz -C /opt/java/


# Set env var for current session
export JAVA_HOME=/opt/java/jdk-21.0.5+11
export PATH=$JAVA_HOME/bin:$PATH

# (optionally) Set env var permanently
echo 'export JAVA_HOME=/opt/java/jdk-21.0.5+11' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc


# Verify
java -version
javac -version
```

### Maven Installation (version 3.9.16 as example)

```
# Download Maven 3.9.16
cd /tmp
wget https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.tar.gz

# Extract
sudo tar -xzf apache-maven-3.9.16-bin.tar.gz -C /opt/

# Set env var for current session
export M2_HOME=/opt/apache-maven-3.9.16
export PATH=$M2_HOME/bin:$PATH

# (optionally) Set env var permanently
echo 'export M2_HOME=/opt/apache-maven-3.9.16' >> ~/.bashrc
echo 'export PATH=$M2_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify (expected to show Maven 3.9.16 and Java 21+)
mvn --version
```

## Building and Testing

### Step-by-step build and test

```
cd /path/to/yang_tests

# Clean previous builds
mvn clean

# Compile source code
mvn compile

# Package into JAR files (skip running tests)
mvn package -DskipTests

# Run yangkit tests
cd yangkit
mvn test

# Run yangtools tests
cd ../yangtools
mvn test
```


### Run individual unittest

```bash
# Run a specific test class
mvn test -Dtest=NotifEnvelopeTest

# Run a specific method
mvn test -Dtest=NotifEnvelopeTest#testValidNotifEnvelope

# Run tests with verbose mode
mvn test -X
```

