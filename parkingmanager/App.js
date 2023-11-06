// App.js
import React from 'react';
import { View, StatusBar } from 'react-native';
import AppNavigator from './AppNavigator';

const App = () => {
  return (
    <View style={{ flex: 1 }}>
      <AppNavigator />
    </View>
  );
};

export default App;
