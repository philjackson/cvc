(ns app.view.privacy
  (:require [app.view.cv :refer [markdown]]))

(defonce pp "# Nextcv LTD Privacy Policy

This Privacy Policy describes how your personal information is
collected, used, and shared when you visit or make a purchase from
https://nextcv.net (the “Site”).

### PERSONAL INFORMATION WE COLLECT

When you visit the Site, we automatically collect certain information
about your device, including information about your web browser, time
zone, and some of the cookies that are installed on your
device. Additionally, as you browse the Site, we collect information
about the individual web pages you view and information about how you
interact with the Site. We refer to this automatically-collected
information as “Device Information.”

### We collect Device Information using the following technologies:

- “Cookies” are data files that are placed on your device or computer
  and often include an anonymous unique identifier. For more
  information about cookies, and how to disable cookies, visit
  http://www.allaboutcookies.org.

Additionally when you sign-up through the Site, we collect certain
information from you, including your name and email address.We refer
to this information as “Person Information.”

When we talk about “Personal Information” in this Privacy Policy, we
are talking both about Device Information and Person Information.

### HOW DO WE USE YOUR PERSONAL INFORMATION?

We use the Person Information that we collect generally to identify
you via The Site. Additionally, we use this Person Information to:
Communicate with you; - When in line with the preferences you have
shared with us, provide you with information or advertising relating
to our products or services.

### SHARING YOUR PERSONAL INFORMATION

- We use Google Analytics to help us understand how our customers use
  the Site -- you can read more about how Google uses your Personal
  Information
  here:https://www.google.com/intl/en/policies/privacy/.You can also
  opt-out of Google Analytics here:
  https://tools.google.com/dlpage/gaoptout.

Finally, we may also share your Personal Information to comply with
applicable laws and regulations, to respond to a subpoena, search
warrant or other lawful request for information we receive, or to
otherwise protect our rights.

### YOUR RIGHTS

If you are a European resident, you have the right to access personal
information we hold about you and to ask that your personal
information be corrected, updated, or deleted. If you would like to
exercise this right, please contact us through the contact information
below.

Additionally, if you are a European resident we note that we are
processing your information in order to fulfill contracts we might
have with you (for example if you make an order through the Site), or
otherwise to pursue our legitimate business interests listed
above.Additionally, please note that your information will be
transferred outside of Europe, including to Canada and the United
States.

### DATA RETENTION

We will maintain your Person Information for our records unless and
until you ask us to delete this information.

### CHANGES

We may update this privacy policy from time to time in order to
reflect, for example, changes to our practices or for other
operational, legal or regulatory reasons.")

(defn privacy-policy []
  [:div (markdown pp)])
