import {React,useState,useEffect} from 'react';
import { View, Text, Image, TouchableOpacity, StyleSheet,Dimensions } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';




const HomeScreen = ({navigation}) => {

  const [loggedIn,setLoggedIn] = useState(null)

  async function clearAsyncStorage() {
    try {
      await AsyncStorage.clear();
      console.log('AsyncStorage has been cleared successfully.');
    } catch (error) {
      console.error('Error clearing AsyncStorage:', error);
    }
  }
 
  async function getLoggedIn() {

    try {
     
      const loggedIn = await AsyncStorage.getItem('@loggedIn');

      setLoggedIn(loggedIn)
      console.log(loggedIn)   
      
    } catch (error) {
      console.log("ERROR WHILE FETCHING loggedIn FROM ASYNC STORAGE" + error.message)
    
    }
    



  }

  useEffect(()=>{
    //clearAsyncStorage()

    getLoggedIn()

  },[])

  useEffect(() => {
    // This code will run after `loggedIn` is set or whenever `loggedIn` changes.
    // You can perform actions here that depend on the `loggedIn` state.
    if (loggedIn !== null) {
      // `loggedIn` has been set, you can do something here.
      // For example, navigate to another screen, make an API request, etc.
      console.log("loggedIn has been set to:", loggedIn);
      // Add your actions here.
      if(loggedIn==="true")
      navigation.navigate("Main")
    }
  }, [loggedIn]);

  return (
    <View style={styles.container}>
      {/* White upper two-thirds */}
      <View style={styles.upperContainer}>
        {/* App name */}
        <Text style={styles.appName}>PARKING MANAGER</Text>
        {/* Image */}
        <Image
          source={require('./logo.png')} // Replace with your image source
          style={styles.image}
        />
      </View>
      {/* Black lower one-third */}
      <View style={styles.lowerContainer}>
        {/* Register button */}
        <TouchableOpacity style={styles.registerButton} onPress={()=>{navigation.navigate("EnterEmail")}}>
          <Text style={styles.buttonText}>REGISTRACIJA</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;


const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  upperContainer: {
    flex: 2,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'white',
  },
  lowerContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#383636',
  },
  appName: {
    fontSize: windowHeight * 0.033, // Responsive font size
    fontWeight: 'bold',
    marginBottom: windowHeight * 0.05, // Responsive spacing
  },
  image: {
    width: windowHeight * 0.33, // Responsive image size
    height: windowHeight * 0.33,
  },
  registerButton: {
    backgroundColor: 'gray',
    paddingVertical: windowHeight * 0.03, // Responsive padding
    paddingHorizontal: windowWidth * 0.1, // Responsive padding
    borderRadius: 10,
    width: '70%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: windowHeight* 0.023, // Responsive font size
    fontWeight: 'bold',
  },
});




export default HomeScreen;
