BUILD_PATH="$PWD"/__build
rm -rf $BUILD_PATH
mkdir "$BUILD_PATH"
SRC_PATH="$PWD"/src
RELATIVE_PATH=com/jetbrains/test

javac -d "$BUILD_PATH" \
 "$SRC_PATH/$RELATIVE_PATH"/*.java \
 "$SRC_PATH/$RELATIVE_PATH"/entities/*.java \
 "$SRC_PATH/$RELATIVE_PATH"/interfaces/*.java

cd $BUILD_PATH

jar cfe ./../graph.jar com.jetbrains.test.Main "$RELATIVE_PATH"/*.class \
 "$RELATIVE_PATH"/entities/*.class \
 "$RELATIVE_PATH"/interfaces/*.class

cd ..
rm -rf $BUILD_PATH
