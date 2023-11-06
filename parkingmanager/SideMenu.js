import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet,Image,Dimensions } from 'react-native';
import ResponsiveImage from 'react-native-responsive-image';
import AsyncStorage from '@react-native-async-storage/async-storage';

const SideMenu = ({elementi, isOpen, onClose , onSelect,vrsta,navigation }) => {
  if (!isOpen) {
    return null; // Render nothing if the menu is closed
  }

  async function odjava() {
  clearAsyncStorage()
  navigation.navigate("Home")

  }

  async function clearAsyncStorage() {
    try {
      await AsyncStorage.clear();
      console.log('AsyncStorage has been cleared successfully.');
    } catch (error) {
      console.error('Error clearing AsyncStorage:', error);
    }
  }

  return ( 
    <View style={styles.sideMenu}>
      {/* Content of the side menu */}
     {"kvart"===vrsta ? <TouchableOpacity  style={styles.izbor} onPress={()=>{onSelect("SVI PARKINZI")}}>
         <Text style={styles.izborText}>SVI PARKINZI</Text>
        
      </TouchableOpacity> :null }

     


      {elementi.map((item, index) => (
         <TouchableOpacity key={item.id} style={styles.izbor} onPress={()=>{onSelect(item.name)}}>
         <Text style={styles.izborText}>{item.name}</Text>
        
         </TouchableOpacity>
        
      ))}

{"grad"===vrsta ? <TouchableOpacity  style={styles.izbor} onPress={()=>{odjava()}}>
      <ResponsiveImage
            source={require('./logout.png')}
            initHeight={30}
            initWidth={30}
          />
         <Text style={styles.izborText}>ODJAVI SE</Text>
        
      </TouchableOpacity> :null }
    
    
      
      {/* Close button */}
      <TouchableOpacity onPress={onClose} style={styles.closeButton}>
        <Text style={styles.closeText}>ZATVORI</Text>
      </TouchableOpacity>

      
    </View>
  );
};

const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

const styles = StyleSheet.create({
  sideMenu: {
    position: 'absolute',
    top: 0,
    left: 0,
    width: 200, // Adjust the width as needed
    height: '100%',
    backgroundColor: 'white',
    padding: windowHeight*0.023,
    zIndex: 999, // Ensure it's on top of other content
    justifyContent:"center",
    alignItems:"center",
    borderColor:"black",
    borderWidth:5
  },
  closeButton: {
    marginTop: windowHeight*0.02,
    backgroundColor:"red",
    padding:2,
    justifyContent:"center",
    alignItems:"center",
    borderRadius:10,
    borderWidth:5,
    borderColor:"red"
  },
  closeText: {
    color:"white",
    fontWeight:'bold',
    fontSize:windowHeight*0.025
  },
  izbor:{
    backgroundColor:"lightgray",
    borderRadius:5,
    borderWidth:2,
    width:"110%",
    justifyContent:"center",
    alignItems:"center",
    flexDirection:"row",
    padding:6,

    margin:5,
    
    
  },
  izborText:{
    fontSize:windowHeight*0.023,
    fontWeight:"bold"


  },

  image:{
    width:windowHeight*0.033,
    height:windowHeight*0.04,
    marginLeft:5
  }
});

export default SideMenu;
