# Grievance Reporting Android App 
Android Chatbot application (looks pretty much like Google Assistant App) which interacts with user using natural language /voice interaction by leveraging Google’s [api.ai](https://api.ai/) NLP platform to auto populate request attributes and then asks users to provide missing details using speech /chat interaction and finally creates ticket into [Diamantedesk](https://diamantedesk.com/getting-started/) Ticketing system. 

<<<short VIDEO HERE>>>

This solution won Hackathon 2017 @ Infosys Chandigarh (India) help in collaboration with Chandigarh Administration.

## Features
### Android App
* Take's input from user via voice as well as text to resolve user's query related to tickets.
* Open Source platform stack : full control over system, code base, easy customizations

### Grievance Reporting Backend (DiamanteDesk)
* DiamanteDesk exposes REST APIs :Full Integration with external systems
* High reliability, flexibility, scalability and extensibility due to latest technology stack usage
* Deep integrations with :
* CRM systems - (OroCRM, Salesforce, SugarCRM, etc), 
* eCommerce solutions - Magento and Prestashop platforms, Shopify, eBay and Amazon marketplaces
* Bug-tracking/project management tools - Jira, YouTrack, Redmine, Basecamp…

### Solution components:
* [Diamantedesk](https://diamantedesk.com/getting-started/) : built on latest Technology Stack - PHP, Symfony2, OroPlatform, Bootstrap, HTML5, CSS3, mySQL DB
* Native Android App (Android Studio IDE)
* [api.ai](https://api.ai/)  - Google’s NLP (Natural Language Processing/ AI/ ML) engine
* Speech Recognition API (Google)

 
The Android app is ready to install and test but in case you would like to setup your own backend, you can do it quickly by following below steps:

## Setup Steps
You would need to do following steps:
* Setup DiamanteDesk App as explained here
* Setup API.AI App as explained here
* Modify Android app configuration to point to DiamanteDesk and API.AI backends

### Contributing  
Please raise an issue of the requirement so that a discussion can take before any code is written, 
even if you intend to raise a pull request.

Please see Sample app for full example.

## To-Do

* The app currently doesn't support OTP verification so requires SMS gateway and backend integration
* The bot is currently not multi-lingual

## License

MIT

