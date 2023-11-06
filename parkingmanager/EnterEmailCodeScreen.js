
import { View, Text, Image, TextInput, TouchableOpacity, StyleSheet, Dimensions } from 'react-native';
import { NavigationContext } from 'react-navigation';
import React ,{useState, useEffect} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import ResponsiveImage from 'react-native-responsive-image';


LOGIN_I_OSTALO_API="https://app.pro-max.hr:9443"


const { width, height } = Dimensions.get('window'); // Get screen dimensions

const HomeScreen = ({navigation}) => {
  const axiosInstance = axios.create({
    withCredentials: true,
  });

  const [code, setCode] = React.useState('');
  const[email,setEmail]= useState("")
  const[err,setErr] = useState("")

  async function getEmail() {

    try {
     
      const emailAsync = await AsyncStorage.getItem('@email');

      setEmail(emailAsync)
      
      
    } catch (error) {
      console.log("ERROR WHILE FETCHING MAIL FROM ASYNC STORAGE" + error.message)
    }
    



  }

  useEffect(()=>{

    getEmail()

  },[])

  async function setLoggedIn() {
    try{
    AsyncStorage.setItem("@loggedIn","true")}
    catch(error) {
      console.log("Greska pri setanju loggedIn true")
    }
  }

  async function storeCookie (cookie) {
    const value=JSON.stringify(cookie).split(";")[0].slice(15) // promijeni ovo ako se pormijeni ime cookieja
    try {
      AsyncStorage.setItem("@Cookie", value)
    }
    catch(error) {
      console.log("Error while setting cookie in async storage "+error)

    }


  }

  async function sendCode() { 

  const verificationMap = {


    email: email, // Replace with the actual email
    code: code, // Replace with the actual verification code
  };

  console.log("Saljem mail "+email+ " i code "  + code)
  
  // Define the URL of the API endpoint
  const apiUrl = LOGIN_I_OSTALO_API+"/emailVerification/verify-code";
  
  // Send the POST request
  axiosInstance.post(apiUrl, verificationMap)
    .then((response) => {
      // Handle the successful response here
      console.log('Response:', response.data);

      const cookie = response.headers['set-cookie'];

      console.log("COOKIES "+ cookie)

      storeCookie(cookie).then(()=>{
        setLoggedIn();
        if(response.status==200) {
          navigation.navigate("Main")
        }
      })

     
  
    })
    .catch((error) => {
      // Handle any errors that occurred during the request
      console.error('Error:', error.response.data.error);
      setErr(error.response.data.error)

    });
  }



  return (
    <View style={styles.container}>
      {/* White upper portion */}
      <View style={styles.upperContainer}>
        <Text style={{color:"red",fontWeight:"bold",fontSize:windowHeight*0.02}}>{err==null ? null : err}</Text>
        <TouchableOpacity onPress={()=>{navigation.navigate("EnterEmail")}}>
        <ResponsiveImage initHeight={30} initWidth={30} source={require('./back.png')} style={styles.back} /> 
        </TouchableOpacity>
        {/* Enter email text */}
        <View style={styles.textContainer}><Text style={styles.enterEmailText}>Na mail ti je poslan šesteroznamenkasti kod, upiši ga da te autentificiramo</Text></View>
        
        {/* Email input field */}
        <TextInput
          style={styles.emailInput}
          placeholder="Kod"
          onChangeText={(text) => setCode(text)}
          value={code}
        />
        {/* Register button */}
        <TouchableOpacity style={styles.registerButton} onPress={()=>{sendCode()}}>
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

const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

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
  textContainer: {
    width: windowWidth * 0.67, // Use a percentage of the screen width
  },
  lowerContainer: {
    flex: 2, // Adjust the ratio as needed
    alignItems: 'center',
    justifyContent: 'center',
  },
  enterEmailText: {
    fontSize: windowHeight * 0.02, // Responsive font size
    fontStyle: 'italic',
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
  back: {
   
    marginBottom: windowHeight * 0.02, // Responsive spacing
    marginTop: windowHeight * 0.01, // Responsive spacing
  },
});
export default HomeScreen;
