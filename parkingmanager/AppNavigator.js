// AppNavigator.js
import { createStackNavigator } from 'react-navigation-stack';
import { createAppContainer } from 'react-navigation';

import HomeScreen from './HomeScreen';
import EnterEmailScreen from './EnterEmailScreen';
import EnterEmailCodeScreen from "./EnterEmailCodeScreen"
import MainScreen from "./MainScreen"
import DetaljiScreen from "./Detalji"

const AppNavigator = createStackNavigator(
  {
    Home: HomeScreen,
    EnterEmail: EnterEmailScreen,
    EnterEmailCode:EnterEmailCodeScreen,
    Main:MainScreen,
    Detalji:DetaljiScreen
  },
  {
    initialRouteName: 'Home',
    defaultNavigationOptions: {
        headerShown: false, // Hide the header for all screens
      },
  }
);

export default createAppContainer(AppNavigator);
