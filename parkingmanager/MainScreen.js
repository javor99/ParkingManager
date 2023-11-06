import React ,{useState, useEffect} from 'react';
import {RefreshControl, View, Text, Image, StyleSheet,TouchableOpacity,Dimensions, ActivityIndicator } from 'react-native';
import { ScrollView } from 'react-native-gesture-handler';
import SideMenu from './SideMenu.js'
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as Location from 'expo-location';
import ResponsiveImage from 'react-native-responsive-image';

const PARKING_API="https://app.pro-max.hr:9443"
const LOGIN_I_OSTALO_API="https://app.pro-max.hr:9443"


//SUTRA - dodaj da moze mijenjat defualt grad i default hood za svaki grad, kruzic koji se vrti dok loada parkinge i kad scrolla gore da se refresha, kasnije ces dodat da se renderaju po udaljenosti od korisnika 
const App = ({navigation}) => {
  const axiosInstance = axios.create();
    const[gradIzbronik,setGradIzbronik]=useState(false)
    const[kvartIzbronik,setKvartIzbronik]=useState(false)
    const[izabraniGrad,setIzabraniGrad] = useState(null)
    const[izabraniKvart,setIzabraniKvart]=useState(null)
    const[gradoviIKvartovi,setGradoviIKvartovi]=useState(null);
    const[gradovi,setGradovi]=useState(null)
    const[kvartovi,setKvartovi] = useState([])
    const[defaultGrad,setDefaultGrad] = useState("Zagreb")
    const[defaultHoods,setDefaultHoods] = useState(null)
    const[pocetak,setPocetak] = useState(true)
    const[aktivniParkinzi,setAktivniParkinzi] = useState(null)
    const[loading,setLoading] = useState(false)
    const[cookie,setCookie] = useState(null);
     const[position,setPosition] = useState(null)
    const[sortedAktivniParkizni,setSortedAktivniParkinzi] = useState(null) 
    const[radi,setRadi] =  useState(true)
    const[prviLoading,setPrviLoading] = useState(true)
    const[loadingRefresh,setLoadingRefresh] = useState(false)
    const[positionDenied,setPositionDenied] = useState(false)

    const onRefresh = React.useCallback(() => {
      setLoading(true);
    
      setTimeout(() => {
        loadParkingData();
    
        // Delay setting prviLoading to false after the animation is complete
        requestAnimationFrame(() => {
          setLoading(false);
        
        });
      }, 2000);
    }, []);

    function refreshGumb() {
      setLoadingRefresh(true);
    
      setTimeout(() => {
        loadParkingData();
    
        // Delay setting prviLoading to false after the animation is complete
        requestAnimationFrame(() => {
          setLoadingRefresh(false);
        
        });
      }, 2000);

    } 
    

    async function getLokacija() {
      try {
        const { status } = await Location.requestForegroundPermissionsAsync();
    
        if (status !== 'granted') {
          console.log('Location permission denied');
          setPositionDenied(true);
          setRadi(false);
          return false; // Permission not granted
        } else {
          const location = await Location.getCurrentPositionAsync({});
          console.log(location);
          const { latitude, longitude } = location.coords;
          setPosition({ latitude, longitude });
          return true; // Location gained
        }
      } catch (err) {
        console.error('Error while fetching location:', err);
        setPositionDenied(true);
        setRadi(false);
        return false; // Error occurred
      }
    }
    
  // function getLokacija() { // ZA SIMULATOR
    //setPosition({"latitude":20,"longitude":20})

   //}

  

  
    
      function detalji(parking) {
      
        navigation.navigate("Detalji",{parking})
      }

      const calculateDistance = (userLocation, objectCoordinates) => {
        const R = 6371; // Radius of the Earth in kilometers
    const lat1 = (Math.PI * userLocation.latitude) / 180;
    const lat2 = (Math.PI * userLocation.latitude) / 180;
    const lon1 = (Math.PI * parseFloat(objectCoordinates.split(",")[0])) / 180;
    const lon2 = (Math.PI * parseFloat(objectCoordinates.split(",")[1])) / 180;

    const dLat = lat2 - lat1;
    const dLon = lon2 - lon1;

    const a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    const distance = R * c; // Distance in kilometers
    console.log("UDALJENOST JE")
    console.log(distance*1000)

    return distance * 1000; // Convert to meters

      };

      useEffect(()=>{
        if(position!=null) {
          const sortedList = aktivniParkinzi.sort((a, b) => {
            const distanceA = calculateDistance(position, a.kordinate);
            const distanceB = calculateDistance(position, b.kordinate);
          
            return distanceA - distanceB; // Sort in ascending order ( to farthest)
          });

          console.log("SORTED LISTA JE : ")
          console.log(sortedList)
          const distanceSortedList = sortedList.map((item) => {
            return { ...item, udaljenost: Number((calculateDistance(position,item.kordinate)/1000).toFixed(2)) };
          });
          setLoading(false)
        console.log("LOADANJE GOTOVO");
          

        

          setSortedAktivniParkinzi(distanceSortedList)
          setPrviLoading(false);
        }

      },[position])
    
    

   async function  generateDefaultHoods() {
    try {
      console.log("ULAZIM U FUNKCIJU KOJA POSTAVLJA DEFUALT HOODOVE")
      const defHoods = [];
      console.log("GRADOVI ZA KOJE POSTAVLJAM DEFUALT HOODOVE SU "+ JSON.stringify(gradovi) )
    
      // Use Promise.all to parallelize AsyncStorage.getItem calls
      const asyncGetItems = gradovi.map(async (grad) => {
        console.log("GLEDAM DEFUALT HOOD ZA GRAD "+grad.name)
        const hood = await AsyncStorage.getItem('@defaultHood' + grad.name);
    
        if (hood !== null) {
          console.log("DEFUALT HOOD ZA GRAD "+ grad.name +" POSTOJI, NJEGOVO IME JE "+hood)
          defHoods.push(grad.name + '%' + hood);
        } else {
          console.log("DEFAULT HOOD ZA GRAD "+grad.name+" NE POSTOJI");
          console.log('POSTAVLJAM DEFUALT HOOD ZA GRAD '+ grad.name +" " +gradoviIKvartovi.filter((item) => item.city === grad.name)[0].name);
          defHoods.push(grad.name + '%' + gradoviIKvartovi.filter((item) => item.city === grad.name)[0].name);
        }
      });
    
      // Wait for all async operations to complete
      await Promise.all(asyncGetItems);

      console.log("ZAVRSIO SAM SA POSTAVLJANJEM DEFAULT HOODOVA ZA GRADOVE , ONI SU: "+defHoods)

      console.log("SETAM DEFAULT HOODS...")
      setDefaultHoods(defHoods);
      console.log('IZLAZIM IZ FUNKCIJE KOJA POSTAVLJA DEFUALT HOODOVE');
      
    } catch (error) {
      // Handle errors here
      console.error('NAISAO SAM NA GRESKU PRILIKOM POSTAVLJANJA DEFAULT HOODOVA', error);
      setRadi(false)
    }
  }
  useEffect(()=>{

    //clearAsyncStorage();
  if(defaultHoods!=null) {

    console.log("USPJESNO SETANJE DEFUALT HOODOVA, ONI SU: "+defaultHoods)
    if(pocetak) {
    console.log("SETAM AKTIVNI GRAD NA " + defaultGrad)
    setIzabraniGrad(defaultGrad.toUpperCase());
    setPocetak(false); }
  }

  },[defaultHoods])
    
    
    
    function getHoodsByCity(targetCity,data) {
      console.log("ULAZIM U FUNKCIJU KOJA TRAZI HOODOVE ZA GRAD "+targetCity)
        //console.log(data)
      // Filter the array to get objects with the specified city
      const filteredData = data.filter((item) => item.city === targetCity);
    
      // Extract names from the filtered objects
     

      console.log("NASAO SAM IH, ONI SU:")
      filteredData.forEach(function(element) {
        console.log(element.name);
    });

    
      return filteredData;
    }
    

    const updateGrad = (text) => {
      console.log("SETAM AKTIVNI GRAD NA "+text+"....")
        setIzabraniGrad(text.toUpperCase());
      

      };

      const updateKvart = (text) => {
        console.log("SETAM AKTIVNI KVART NA "+text+"....")
        setIzabraniKvart(text);
      };

      const setDefaultGradFunc= async (grad) => {
        try {
          console.log("SETAM DEFUALT GRAD NA " + grad)
          await AsyncStorage.setItem('@defaultGrad', grad);
          setDefaultGrad(grad)
        } catch (error) {
          console.log("DOŠLO JE DO POGRESKE PRILIKOM SETANJA DEFUALT GRADA")
          setRadi(false)
        }


      }


      // treba loadat gradove, naselja i parking informaciju za defualt grad - za sobom povlaci i defualt kvart - fetch cities, fetch all hoods for cities, fetch all parking info for a hood
      

      const loadInitialGradoviData = async () =>{
        try {
          console.log("GLEDA JEL IMA STA U ASYNC STORAGEU OD GRADOVA")
          const grad = await AsyncStorage.getItem('@defaultGrad');
          
          if (grad !== null) {
            console.log("NASAO SAM GA, DEFUALT GRAD JE "+grad)
            
            // Data found, do something with it
            setDefaultGrad(grad)
            //console.log(value);
          } else {
            // Data does not exist

            console.log("NEMA NISTA , POSTAVLJAM DEFAULT GRAD NA RIJEKA")
            setDefaultGrad("Rijeka")
          }
        } catch (error) {
          // Handle error
          setRadi(false)
        }
        
        console.log("Axios city get cookie je "+cookie)
        
        axiosInstance.get(LOGIN_I_OSTALO_API+'/cities/all', {
          headers: {
        
            Cookie: "specialToken="+cookie + "; Path=/",
          },
          withCredentials: false        })
      .then(response => {
        console.log("FETCHAO SAM GRADOVE, GRADOVI SU: "+JSON.stringify(response.data))
        console.log("SETAM GRADOVE...")
        setGradovi(response.data)
      })
      .catch(error => {
        // Handle errors
      
      console.log("DOK SAM FETHCAO GRADOVE NAISAO SAM NA GRESKU "+error.message)
      setRadi(false)
      });
    
      }

      const loadInitialHoodsData =  () => {
        axiosInstance.get(LOGIN_I_OSTALO_API+'/hoods/getHoods/all',{ 
          headers: {
        
            Cookie: "specialToken="+cookie + "; Path=/",
          },
          withCredentials: false        })
      
        .then(response => {
          // Update the component's state with the fetched data
          //console.log(response.data)
          console.log("FETHCAO SAM KVARTOVE, KVAROTVI SU : "+  JSON.stringify(response.data))
          console.log("SETAM GRADOVEIKVARTOVE...")
          setGradoviIKvartovi(response.data)
          
         
          
          
        })
        .catch(error => {
          // Handle errors
          console.log("DOK SAM FETHCAO KVARTOVE NAISAO SAM NA GRESKU "+error.message)
          setRadi(false)
          
        
        });
      }

      async function getCookie() {

        try{
          cookieTemp= await AsyncStorage.getItem("@Cookie")
          setCookie(cookieTemp);
        }

        catch(error) {
          console.log("Error while getting cookie from async storage "+error)
          setRadi(false)
        }

      }

      useEffect(()=>{

        getCookie()
       
        //Setaj state position

      },[])

      useEffect(() => {
        (async () => {
          
          let { status } = await Location.requestForegroundPermissionsAsync();
          if (status !== 'granted') {
            setErrorMsg('Permission to access location was denied');
            return;
          }
          
          


        })();
      }, []);

      

      useEffect(()=>{
        if(cookie!=null) {


        
        console.log("<---------------------------------------------------------------------------->")
        console.log("<---------------------------------------------------------------------------->")
        console.log("COOKIE NABAVLJEN ON JE "+cookie)
       

        //console.log("SETAM AXIOS HEADER SA NABAVLJENIM COOKIEM "+cookieWithoutBrackets)
        //axiosInstance.defaults.headers.common['Cookie'] = cookieWithoutBrackets;
        console.log("APLIKACIJA POKRENUTA, POCINJEM LOADAT INICIJALNE PODATKE ZA GRADOVE")
        loadInitialGradoviData() 
      }
    
    },[cookie])

    useEffect(()=>{
      if(gradovi!=null)
    {
      console.log("GRADOVI SU POSTAVLJENI , POCINJEM LOADAT INICIJALNE PODATKE ZA KVARTOVE")
      loadInitialHoodsData()


    }
    },[gradovi])

    useEffect(()=>{
        
       
      if(gradoviIKvartovi!=null){
        console.log("GRADOVI I KVARTOVI SU SETANI")
        console.log("GRADOVI I KVARTOVI SU: "+ JSON.stringify(gradoviIKvartovi))
        generateDefaultHoods();
      }
      

  },[gradoviIKvartovi])

  function getDefualtHood(cityName) {
    console.log("ULAZIM U FUNKCIJU KOJA NALAZI DEFUALT HOOD ZA GRAD "+cityName)
    for (const cityNeighborhood of defaultHoods) {
      const [city, neighborhood] = cityNeighborhood.split('%');
    
      if (city === cityName) {
        console.log("NASAO SAM DEFUALT HOOD, ON JE "+ neighborhood);
        return neighborhood;
      }
    }
    console.log("NISAM NASAO DEFUALT HOOD!!! ERROR ERROR")
    return "Neighborhood not found";
  };

  async function setDefaultHood(city,hood) {

    console.log("evo me u default funkciji")

    try{

        await AsyncStorage.setItem('@defaultHood' + city,hood)
    }

    
    catch {
        console.log("DOŠLO JE DO GREŠKE PRILIKOM POSTAVLJANJA DEFUALT HOODA!!!")
        setRadi(false)
  
      }


    
   
      var defhoods=[]
      console.log("SETAM DEFUALT HOOD " + hood + " ZA GRAD "+city+"...")
      console.log("DEFUALTS HOODS PRIJE PROMJENE IZGLEDA OVAKO: " + defaultHoods)
     
      for (let i = 0; i < defaultHoods.length; i++) {
        const [currentCity, currentNeighborhood] = defaultHoods[i].split('%');
        if (currentCity === city) {
          
          defhoods.push(currentCity + '%' + hood)
        }
        else {
          defhoods.push(currentCity+ "%" + currentNeighborhood)
        }
      }
        
        console.log("NOVA LISTA DEFUALT HOODOVA SADA IZGLEDA OVAKO "+ defhoods);
        console.log("SETAM NOVI DEFAULT HOODS ...")
        setDefaultHoods(defhoods)
    


   

  }


  
  

    useEffect(() => {

      
        if(izabraniGrad!=null){
          console.log("AKTIVNI GRAD JE USPJESNO SETAN, ON JE: "+izabraniGrad) 
          
          console.log("SETAM AKTIVNE KVARTOVE ZA GRAD "+izabraniGrad +"...")
          setKvartovi(getHoodsByCity(izabraniGrad, gradoviIKvartovi));
          console.log("TOGGLO SAM GRAD IZBORNIK")
          toggleGradIzbornik();
          console.log("SETAM KVART IZBORNIK NA TRUE")
          setKvartIzbronik(true)
      
          console.log("SETAM IZABRANI KVART...")
          setIzabraniKvart(getDefualtHood(izabraniGrad));
        
      
      }
        
        // Load other data or perform other actions related to izabraniGrad change
     
      
    }, [izabraniGrad]);

    const nadiId = (kvart,grad) =>{
      return gradoviIKvartovi.filter(item=>item.name===kvart && item.city===grad)[0].id
    }
    
    loadParkingData = () => {
      console.log("KRECEM FETCHAT PARKING PODATKE - LOADA SE ")
      setLoading(true);

      if(izabraniKvart==="SVI PARKINZI") {

        axiosInstance.get(PARKING_API+'/parkings/getParkingCity/'+izabraniGrad, {
        headers: {
      
          Cookie: "specialToken="+cookie + "; Path=/",
        },
        withCredentials: false })
      .then(response => {
        console.log("FETCHAO SAM PARKINGE, PARKINZI SU: "+JSON.stringify(response.data))
        console.log("SETAM AKTIVNE PARKINGE...")
        setAktivniParkinzi(response.data)
      })
      .catch(error => {
        // Handle errors
      
      console.log("DOK SAM FETHCAO PARKINGE NAISAO SAM NA GRESKU "+error.message)
      setRadi(false)
      });


      }
      else { 
     
      console.log("ID AKTIVNOG HOODA JE "+nadiId(izabraniKvart,izabraniGrad))
      axiosInstance.get(PARKING_API+'/parkings/getParking/'+nadiId(izabraniKvart,izabraniGrad), {
        headers: {
      
          Cookie: "specialToken="+cookie + "; Path=/",
        },
        withCredentials: false })
      .then(response => {
        console.log("FETCHAO SAM PARKINGE, PARKINZI SU: "+JSON.stringify(response.data))
        console.log("SETAM AKTIVNE PARKINGE...")
        setAktivniParkinzi(response.data)
      })
      .catch(error => {
        // Handle errors
      
      console.log("DOK SAM FETHCAO PARKINGE NAISAO SAM NA GRESKU "+error.message)
      setRadi(false)
      });


    } }

    useEffect(()=>{
      if (aktivniParkinzi!=null){
        console.log("AKTIVNI PARKINZI USPJESNO SETANI, ONI SU: " + JSON.stringify(aktivniParkinzi))
        console.log("POSITION JE "+position)
        console.log("")
        
        getLokacija()
        
        
        
        
      }

    }, 
    [aktivniParkinzi])
  

    useEffect(()=>{
      //console.log(izabraniKvart)
      if(izabraniKvart!=null){
        console.log("USPJESNO SAM SETAO AKTIVNI KVART, ON JE "+ izabraniKvart)
        console.log("TOGGLAM KVART IZBORNIK")
      toggleKvartIzbornik()
      console.log("ULAZIM U FUNKCIJU KOJA CE FETCHAT SVE PARKINGE U "+ izabraniKvart)
      loadParkingData()
    }

     
     

  },[izabraniKvart])

    const toggleGradIzbornik = () => {
        if(kvartIzbronik) setKvartIzbronik(!kvartIzbronik); 
        setGradIzbronik(!gradIzbronik)
      };
      const toggleKvartIzbornik = () => {
        if(gradIzbronik) setGradIzbronik(!gradIzbronik); 

        setKvartIzbronik(!kvartIzbronik);
      };
      
      return (
        <View style={styles.container}>
          {(prviLoading && radi) ? 
          <View style={styles.loadingContainer}>
              <ActivityIndicator size="large" />

            </View>
          
          
          :
          radi ? (
            <>
               <View style={styles.upperContainer} >
      <TouchableOpacity  onPress={()=>{toggleGradIzbornik()}}>
        <View style={styles.imageContainer}>
          <ResponsiveImage
            source={require('./city.png')}
            initHeight={50}
            initWidth={50}
          />
        </View>
        </TouchableOpacity>
        <View style={styles.textContainer}>
          <Text style={styles.text}>{ izabraniGrad}</Text>
        </View>
     {/*
      <View style={styles.imageContainer}>
        <TouchableOpacity onPress={() => {setDefaultGradFunc(izabraniGrad)}}>
          <Image
            source={require('./setDefault.png')}
            style={styles.image}
          />
          </TouchableOpacity>
        </View>
        */}

      </View>
      

      {/* 1/12th under upper */}
      <View style={styles.lowerContainer}>
      <TouchableOpacity  onPress={()=>{toggleKvartIzbornik()}}>
        <View style={styles.imageContainer}>
     
          <ResponsiveImage
            source={require('./hood.png')}
            initHeight={50}
            initWidth={50}
          />
        </View>
        </TouchableOpacity>
        <View style={styles.textContainer}>
          <Text style={styles.text}>{izabraniKvart}</Text>
        </View>
        {/* <View style={styles.imageContainer}>
          <TouchableOpacity onPress={()=>{setDefaultHood(izabraniGrad,izabraniKvart)}}>
          <Image
            source={require('./setDefault.png')}
            style={styles.image}
          />
          </TouchableOpacity>
      </View> */}
        </View>

         <TouchableOpacity style={styles.refreshContainer} onPress={()=>{refreshGumb()}}>
          { !loadingRefresh && <Image
            source={require('./refresh.png')}
            style={styles.refreshimage}
          /> }
        </TouchableOpacity>
              <ScrollView
                style={styles.scrollContainer}
                refreshControl={
                  <RefreshControl refreshing={loading} onRefresh={onRefresh} tintColor="black" />
                }
              >
                {loadingRefresh===true ?  <View style={styles.loadingContainer}>
                                            <ActivityIndicator size="large" />

                                              </View>  :sortedAktivniParkizni !== null ? (
                  <>
                    {sortedAktivniParkizni.map((parking) => (
                      <View key={parking.id} style={styles.littleContainer}>
                        <Text style={{ fontWeight: "bold" }}>{parking.name.toUpperCase()}</Text>
                        <View style={styles.slikatext}>
                          <ResponsiveImage initHeight={20} initWidth={20} source={require('./car.png')}  style={styles.imagecar} />
                          <Text style={{ color: parking.brojZauzetih === parking.brojParkinga ? "red" : parking.brojZauzetih < parking.brojParkinga / 2 ? "green" : "#f56342", fontSize: windowHeight * 0.02, fontWeight: "bold" }}>
                            {parking.brojZauzetih}/{parking.brojParkinga}
                          </Text>
                        </View>
                        <Text>UDALJENOST: {parking.udaljenost} KM</Text>
                        <TouchableOpacity style={styles.detaljiButton} onPress={() => { detalji(parking) }}>
                          <Text style={styles.buttonText}>DETALJI</Text>
                        </TouchableOpacity>
                      </View>
                    ))}
                  </>
                ) : null}
              </ScrollView>
            </>
          ) : (
            <View style={styles.errorContainer}>
            <ResponsiveImage initHeight={30} initWidth={30} source={require('./error.png')} style={styles.imagecar} />

              <Text style={styles.errorText}>Network Error</Text>
              {!positionDenied &&<Text style={styles.errorText}>Ili nemas neta ili radimo na serverima.</Text>}
              {positionDenied && <Text style={styles.errorText}>Dozvoli nam korištenje lokacije i restartaj aplikaciju.</Text>}
            
             
            </View>
          )}
          {radi && <SideMenu elementi={gradovi} isOpen={gradIzbronik} onClose={toggleGradIzbornik} onSelect={updateGrad} vrsta="grad"   navigation={navigation} />}
          {radi && <SideMenu elementi={kvartovi} isOpen={kvartIzbronik} onClose={toggleKvartIzbornik} onSelect={updateKvart} vrsta="kvart"   navigation={navigation} />}
        </View>
      );
       
};

const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    
  },
  errorContainer:{
    height:"100%",
    width:"100%",
    alignItems:"center",
    justifyContent:"center"

  },
  loadingContainer:{
    height:windowHeight/2,
    width:"100%",
    alignItems:"center",
    justifyContent:"center",
  

  },
  errorText:{
    fontSize:30,
    color:"red",
    fontWeight:"bold"

  },
  upperContainer: {
    marginTop: windowHeight*0.05,
    backgroundColor: "#D9D9D9",
    height:Dimensions.get("window").height/12,
    flexDirection: 'row',
    borderBottomColor: "black",
    borderBottomWidth: 2,
    borderRightColor:"black",
    borderRightWidth:2,
    borderTopColor: "black",
    borderTopWidth: 2,
    borderLeftColor:"black",
    borderLeftWidth:2,
    paddingLeft:2,
    alignItems:"center",
    justifyContent:"center"
    
  },
  lowerContainer: {
    backgroundColor: "#D9D9D9",
    height:Dimensions.get("window").height/12,
    flexDirection: 'row',
    borderBottomColor: "black",
    borderBottomWidth: 2,
    borderRightColor:"black",
    borderRightWidth:2,
    borderLeftColor:"black",
    borderLeftWidth:2,
    paddingLeft:2,
    alignItems:"center",
    justifyContent:"center"
  },
  slikatext:{
      flexDirection:"row",
      justifyContent:"center",
      alignItems:"center",
      margin:6
  },

  imageContainer: {
    flex: 1,
    alignItems: 'flex-start', // Image on the left
    justifyContent: 'center',
    paddingLeft: 3,
     // Add some padding to the left of the image
  },
  image: {
    width: windowHeight*0.06, // Adjust the width as needed
    height: windowHeight*0.06, // Adjust the height as needed
    resizeMode: 'cover',
  },
  
  imagecar: {
  
    marginRight:4
  },
  textContainer: {
    flex:6,
    justifyContent: 'center',
    alignItems:"flex-start",
    
  },
  text: {
    fontSize: windowHeight*0.02,
    textAlign: 'center',
    textTransform: 'uppercase',
    fontWeight:"bold"
  },
  scrollContainer: {
    flex: 8 / 12, // Rest of the screen,
    
  },
  littleContainer: {
    height: windowHeight*0.18, // Adjust the height of each little container as needed
    marginBottom: windowHeight*0.01,
    width:"100%",
    backgroundColor: 'lightgray',
    alignItems: 'center',
    justifyContent: 'center',
    borderColor:"black",
    borderWidth:2
  },
  detaljiButton:{
      backgroundColor:"white",
      width:"30%",

      justifyContent:"center",
      alignItems:"center",
      borderRadius:20,
      height:"20%",
      marginTop:5
      
  },
  refreshContainer: {
    width:"100%",
    height:windowHeight*0.05,
    alignItems:"center",
    justifyContent:"center"
    

  },
  buttonText:{
      fontSize:windowHeight*0.02,
      fontWeight:"bold"
  },
  menucontainer:{
      height:"100%",
      backgroundColor:"red",
      width:"30%",
      justifyContent:"center"
      
  },
  refreshimage:{
    height:windowWidth*0.05,
    width:windowWidth*0.05
  }
});

export default App;
