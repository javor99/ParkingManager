import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Image,
  Dimensions,
  Linking,
} from 'react-native';
import { ScrollView } from 'react-native-gesture-handler';
import ResponsiveImage from 'react-native-responsive-image';

const Detalji = ({ navigation }) => {
  const parking = navigation.state.params.parking;

  const otvoriGoogleMaps = () => {
    if (parking) {
      const [latitude, longitude] = parking.kordinate.split(',');
      const url = `https://www.google.com/maps/dir/?api=1&destination=${latitude},${longitude}&travelmode=driving`;
      Linking.openURL(url)
        .catch((err) => console.error('Error opening Google Maps:', err));
    }
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={() => navigation.navigate('Main')}>
        <ResponsiveImage source={require('./back.png')} initHeight={30} initWidth={30}  style={styles.backButton} />
      </TouchableOpacity>

      {parking && (
        <ScrollView style={{maxHeight:windowHeight/1.6,width:"90%",marginBottom:20}}>
        <View style={styles.infoContainer}>
          <Text style={styles.title}>{parking.name}</Text>
          {renderInfo("VRIJEME ZADNJEG DOHVAĆANJA PODATAKA", parking.lastUpdateTime)}
          {renderInfo("CIJENA", parking.cijenaParkinga)}
          {renderInfo("NAČINI PLAĆANJA", parking.naciniPlacanja.trim())}
          {renderInfo("VRIJEME NAPLATE", parking.vrijemeNaplate.trim())}
          {renderInfo("BROJ ZAUZETIH MJESTA", parking.brojZauzetih)}
          {renderInfo("BROJ UKUPNIH MJESTA", parking.brojParkinga)}
        </View>
        </ScrollView>
      )}

      <TouchableOpacity onPress={otvoriGoogleMaps} style={styles.button}>
        <ResponsiveImage source={require('./location.png')} initHeight={40} initWidth={30} style={styles.icon} />
        <Text style={styles.buttonText}>OTVORI NA GOOGLE KARTI</Text>
      </TouchableOpacity>
    </View>
  );
};

const renderInfo = (title, content) => (
  <View style={styles.infoLine}>
    <Text style={styles.infoTitle}>{title}</Text>
    <View style={styles.infoContent}>
      <Text style={styles.infoContentText}>{content}</Text>
    </View>
  </View>
);

const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#F5F5F5', // Light gray background
  },
  backButton: {
   
    marginBottom: 20,
  },
  infoContainer: {
    alignItems: 'center',
    marginBottom: 20,
    backgroundColor: 'white', // White background for info
    padding: 16,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 4,
    height:"100%"
    
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333', // Dark text color
  },
  infoLine: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginVertical: 8,
  },
  infoTitle: {
    flex: 1,
    fontSize: 16,
    color: '#555', // Medium gray text color
  },
  infoContent: {
    flex: 2,
    borderBottomWidth: 1,
    borderColor: '#DDD', // Light gray border
    padding: 4,
    justifyContent: 'center',
    alignItems: 'center',
    
  },
  infoContentText: {
    fontSize: 16,
    color: '#333', // Dark text color
  },
  button: {
    backgroundColor: '#007bff',
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderRadius: 5,
    marginTop: 2,
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  icon: {
    marginRight: 10,
   
  },
});

export default Detalji;
