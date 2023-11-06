
import { View, Text, Image, TextInput, TouchableOpacity, StyleSheet, Dimensions } from 'react-native';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import React ,{useState, useEffect} from 'react';
import { setStatusBarNetworkActivityIndicatorVisible } from 'expo-status-bar';


LOGIN_I_OSTALO_API="https://app.pro-max.hr:9443"

const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

const HomeScreen = ({navigation}) => {
  const [email, setEmail] = useState('');
  const [err, setErr] = useState('');
 
  async function posaljiMail() {

    console.log(email)
    const apiUrl = LOGIN_I_OSTALO_API+"/emailVerification/send-verification-code"

    const emailMap = {
      email: email, // Replace with the actual email
    };

    const emailRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    if (!emailRegex.test(email)) {
      console.log("nista")
      setErr("Neogovarajući format maila!")
      return
     }

    try {
  
      await AsyncStorage.setItem('@email', email);
      
    } catch (error) {
      console.log("DOŠLO JE DO POGRESKE PRILIKOM ASYNC STORANJA MAILA " +error.message)
    }


        
    axios.post(apiUrl, emailMap)
  .then((response) => {
    // Handle the successful response here
    console.log('Response:', response.data);
    navigation.navigate("EnterEmailCode")
  })
  .catch((error) => {
    // Handle any errors that occurred during the request
    console.error('Error:', error.message);
  });


  }

  return (
    <View style={styles.container}>
      {/* White upper portion */}
      <View style={styles.upperContainer}>
        <View style={styles.errorcontainer}>
          <Text style={styles.error}>{err}</Text>
        </View>
        {/* Enter email text */}
        <Text style={styles.enterEmailText}>Upiši email</Text>
        {/* Email input field */}
        <TextInput
          style={styles.emailInput}
          placeholder="Email"
          onChangeText={(text) => setEmail(text)}
          value={email}
        />
        {/* Register button */}
        <TouchableOpacity style={styles.registerButton} onPress={()=>{posaljiMail()}}>
          <Text style={styles.buttonText}>DALJE</Text>
        </TouchableOpacity>
      </View>
      {/* Black lower portion with image */}
      <View style={[styles.lowerContainer, { backgroundColor: '#383636' }]}>
        {/* Image */}
        <Image
          source={require('./darkcar.png')} // Replace with your image source
          style={styles.image}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  upperContainer: {
    flex: 3, // Adjust the ratio as needed
    backgroundColor: 'white',
    alignItems: 'center',
    justifyContent: 'center',
  },
  errorcontainer:{
    justifyContent:"center",
    margin:7,
    alignItems:"center"
  },
  error:{
    color:"red"
  },
  lowerContainer: {
    flex: 2, // Adjust the ratio as needed
    alignItems: 'center',
    justifyContent: 'center',
  },
  enterEmailText: {
    fontSize: windowHeight * 0.03, // Responsive font size
    fontWeight: 'bold',
    marginBottom: windowHeight * 0.02, // Responsive spacing
  },
  emailInput: {
    width: windowWidth * 0.8, // Responsive width
    height: windowHeight * 0.05, // Responsive height
    borderColor: 'gray',
    borderWidth: 1,
    padding: windowWidth * 0.02, // Responsive padding
    marginBottom: windowHeight * 0.02, // Responsive spacing
  },
  registerButton: {
    backgroundColor: 'gray',
    width: windowWidth * 0.35, // Responsive width
    height: windowHeight * 0.07, // Responsive height
    borderRadius: (windowHeight * 0.1) / 2, // Make it semi-circular
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonText: {
    color: 'white',
    fontSize: windowHeight * 0.023, // Responsive font size
    fontWeight: 'bold',
  },
  image: {
    width: '100%',
    height: '100%',
    resizeMode: 'contain',
  },
});

export default HomeScreen;
