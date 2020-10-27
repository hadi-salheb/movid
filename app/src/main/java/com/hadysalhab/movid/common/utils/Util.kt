package com.hadysalhab.movid.common.utils

import android.content.Context
import android.util.DisplayMetrics
import com.hadysalhab.movid.movies.VideosResponse

fun convertDpToPixel(dp: Int, context: Context): Int {
    return dp * (context.resources
        .displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun getYoutubeTrailerFromResponse(videoResponse: VideosResponse) =
    videoResponse.videos.find {
        it.site == "YouTube" && it.type == "Trailer"
    }

fun getVoteAverageNumbers(): List<String> {
    val numbers = mutableListOf<String>()
    for (i in 0..20) {
        numbers.add((i / 2.0).toString())
    }
    return numbers
}


fun getRatingOptions(): List<Double> {
    val options = mutableListOf<Double>()
    for (i in 1..20) {
        options.add((i / 2.0))
    }
    return options
}


fun getPrivacyPolicyHtml() = " <h2>Privacy policy</h2>\n" +
        "<p> This privacy policy (&quot;Policy&quot;) describes how the personally identifiable information (&quot;Personal Information&quot;) you may provide in the &quot;Movid&quot; mobile application (&quot;Mobile Application&quot; or &quot;Service&quot;) and any of its related products and services (collectively, &quot;Services&quot;) is collected, protected and used. It also describes the choices available to you regarding our use of your Personal Information and how you can access and update this information. This Policy is a legally binding agreement between you (&quot;User&quot;, &quot;you&quot; or &quot;your&quot;) and this Mobile Application developer (&quot;Operator&quot;, &quot;we&quot;, &quot;us&quot; or &quot;our&quot;). By accessing and using the Mobile Application and Services, you acknowledge that you have read, understood, and agree to be bound by the terms of this Agreement. This Policy does not apply to the practices of companies that we do not own or control, or to individuals that we do not employ or manage.</p>\n" +
        "<h2>Automatic collection of information</h2>\n" +
        "<p>When you use the Mobile Application, our servers automatically record information that your device sends. This data may include information such as your device\\'s IP address and location, device name and version, operating system type and version, language preferences, information you search for in the Mobile Application, access times and dates, and other statistics.</p>\n" +
        "<p>Information collected automatically is used only to identify potential cases of abuse and establish statistical information regarding the usage of the Mobile Application and Services. This statistical information is not otherwise aggregated in such a way that would identify any particular user of the system.</p>\n" +
        "<h2>Collection of personal information</h2>\n" +
        "<p>You can access and use the Mobile Application and Services without telling us who you are or revealing any information by which someone could identify you as a specific, identifiable individual. If, however, you wish to use some of the features in the Mobile Application, you may be asked to provide certain Personal Information (for example, your name and e-mail address). We receive and store any information you knowingly provide to us when you create an account,  or fill any online forms in the Mobile Application. When required, this information may include the following:</p>\n" +
        "<ul>\n" +
        "<li>Personal details such as name, country of residence, etc.</li>\n" +
        "<li>Contact information such as email address, address, etc.</li>\n" +
        "<li>Account details such as user name, unique user ID, password, etc.</li>\n" +
        "<li>Geolocation data such as latitude and longitude.</li>\n" +
        "</ul>\n" +
        "<p>Some of the information we collect is directly from you via the Mobile Application and Services. However, we may also collect Personal Information about you from other sources such as public databases and our joint marketing partners. You can choose not to provide us with your Personal Information, but then you may not be able to take advantage of some of the features in the Mobile Application. Users who are uncertain about what information is mandatory are welcome to contact us.</p>\n" +
        "<h2>Use and processing of collected information</h2>\n" +
        "<p>In order to make the Mobile Application and Services available to you, or to meet a legal obligation, we need to collect and use certain Personal Information. If you do not provide the information that we request, we may not be able to provide you with the requested products or services. Any of the information we collect from you may be used for the following purposes:</p>\n" +
        "<ul>\n" +
        "<li>Create and manage user accounts</li>\n" +
        "<li>Respond to inquiries and offer support</li>\n" +
        "<li>Improve user experience</li>\n" +
        "<li>Run and operate the Mobile Application and Services</li>\n" +
        "</ul>\n" +
        "<p>Processing your Personal Information depends on how you interact with the Mobile Application and Services, where you are located in the world and if one of the following applies: (i) you have given your consent for one or more specific purposes; this, however, does not apply, whenever the processing of Personal Information is subject to California Consumer Privacy Act or European data protection law; (ii) provision of information is necessary for the performance of an agreement with you and/or for any pre-contractual obligations thereof; (iii) processing is necessary for compliance with a legal obligation to which you are subject; (iv) processing is related to a task that is carried out in the public interest or in the exercise of official authority vested in us; (v) processing is necessary for the purposes of the legitimate interests pursued by us or by a third party.</p>\n" +
        "<p> Note that under some legislations we may be allowed to process information until you object to such processing (by opting out), without having to rely on consent or any other of the following legal bases below. In any case, we will be happy to clarify the specific legal basis that applies to the processing, and in particular whether the provision of Personal Information is a statutory or contractual requirement, or a requirement necessary to enter into a contract.</p>\n" +
        "<h2>Disclosure of information</h2>\n" +
        "<p> Depending on the requested Services or as necessary to complete any transaction or provide any service you have requested, we may share your information with your consent with our trusted third parties that work with us, any other affiliates and subsidiaries we rely upon to assist in the operation of the Mobile Application and Services available to you. We do not share Personal Information with unaffiliated third parties. These service providers are not authorized to use or disclose your information except as necessary to perform services on our behalf or comply with legal requirements. We may share your Personal Information for these purposes only with third parties whose privacy policies are consistent with ours or who agree to abide by our policies with respect to Personal Information. These third parties are given Personal Information they need only in order to perform their designated functions, and we do not authorize them to use or disclose Personal Information for their own marketing or other purposes.</p>\n" +
        "<p>We will disclose any Personal Information we collect, use or receive if required or permitted by law, such as to comply with a subpoena, or similar legal process, and when we believe in good faith that disclosure is necessary to protect our rights, protect your safety or the safety of others, investigate fraud, or respond to a government request.</p>\n" +
        "<h2>Retention of information</h2>\n" +
        "<p>We will retain and use your Personal Information for the period necessary to comply with our legal obligations, resolve disputes, and enforce our agreements unless a longer retention period is required or permitted by law. We may use any aggregated data derived from or incorporating your Personal Information after you update or delete it, but not in a manner that would identify you personally. Once the retention period expires, Personal Information shall be deleted. Therefore, the right to access, the right to erasure, the right to rectification and the right to data portability cannot be enforced after the expiration of the retention period.</p>\n" +
        "<h2>Transfer of information</h2>\n" +
        "<p>Depending on your location, data transfers may involve transferring and storing your information in a country other than your own. You are entitled to learn about the legal basis of information transfers to a country outside the European Union or to any international organization governed by public international law or set up by two or more countries, such as the UN, and about the security measures taken by us to safeguard your information. If any such transfer takes place, you can find out more by checking the relevant sections of this Policy or inquire with us using the information provided in the contact section.</p>\n" +
        "<h2>The rights of users</h2>\n" +
        "<p>You may exercise certain rights regarding your information processed by us. In particular, you have the right to do the following: (i) you have the right to withdraw consent where you have previously given your consent to the processing of your information; (ii) you have the right to object to the processing of your information if the processing is carried out on a legal basis other than consent; (iii) you have the right to learn if information is being processed by us, obtain disclosure regarding certain aspects of the processing and obtain a copy of the information undergoing processing; (iv) you have the right to verify the accuracy of your information and ask for it to be updated or corrected; (v) you have the right, under certain circumstances, to restrict the processing of your information, in which case, we will not process your information for any purpose other than storing it; (vi) you have the right, under certain circumstances, to obtain the erasure of your Personal Information from us; (vii) you have the right to receive your information in a structured, commonly used and machine readable format and, if technically feasible, to have it transmitted to another controller without any hindrance. This provision is applicable provided that your information is processed by automated means and that the processing is based on your consent, on a contract which you are part of or on pre-contractual obligations thereof.</p>\n" +
        "<h2>The right to object to processing</h2>\n" +
        "<p>Where Personal Information is processed for the public interest, in the exercise of an official authority vested in us or for the purposes of the legitimate interests pursued by us, you may object to such processing by providing a ground related to your particular situation to justify the objection.</p>\n" +
        "<h2>Data protection rights under GDPR</h2>\n" +
        "<p>If you are a resident of the European Economic Area (EEA), you have certain data protection rights and the Operator aims to take reasonable steps to allow you to correct, amend, delete, or limit the use of your Personal Information. If you wish to be informed what Personal Information we hold about you and if you want it to be removed from our systems, please contact us. In certain circumstances, you have the following data protection rights:</p>\n" +
        "<ul>\n" +
        "<li>You have the right to request access to your Personal Information that we store and have the ability to access your Personal Information.</li>\n" +
        "<li>You have the right to request that we correct any Personal Information you believe is inaccurate. You also have the right to request us to complete the Personal Information you believe is incomplete.</li>\n" +
        "<li>You have the right to request the erase your Personal Information under certain conditions of this Policy.</li>\n" +
        "<li>You have the right to object to our processing of your Personal Information.</li>\n" +
        "<li> You have the right to seek restrictions on the processing of your Personal Information. When you restrict the processing of your Personal Information, we may store it but will not process it further.</li>\n" +
        "<li> You have the right to be provided with a copy of the information we have on you in a structured, machine-readable and commonly used format.</li>\n" +
        "<li> You also have the right to withdraw your consent at any time where the Operator relied on your consent to process your Personal Information.</li>\n" +
        "</ul>\n" +
        "<p>You have the right to complain to a Data Protection Authority about our collection and use of your Personal Information. For more information, please contact your local data protection authority in the European Economic Area (EEA).</p>\n" +
        "<h2>California privacy rights</h2>\n" +
        "<p>In addition to the rights as explained in this Policy, California residents who provide Personal Information (as defined in the statute) to obtain products or services for personal, family, or household use are entitled to request and obtain from us, once a calendar year, information about the Personal Information we shared, if any, with other businesses for marketing uses. If applicable, this information would include the categories of Personal Information and the names and addresses of those businesses with which we shared such personal information for the immediately prior calendar year (e.g., requests made in the current year will receive information about the prior year). To obtain this information please contact us.</p>\n" +
        "<h2>How to exercise these rights</h2>\n" +
        "<p>Any requests to exercise your rights can be directed to the Operator through the contact details provided in this document. Please note that we may ask you to verify your identity before responding to such requests. Your request must provide sufficient information that allows us to verify that you are the person you are claiming to be or that you are the authorized representative of such person. You must include sufficient details to allow us to properly understand the request and respond to it. We cannot respond to your request or provide you with Personal Information unless we first verify your identity or authority to make such a request and confirm that the Personal Information relates to you.</p>\n" +
        "<h2>Privacy of children</h2>\n" +
        "<p>We do not knowingly collect any Personal Information from children under the age of 18. If you are under the age of 18, please do not submit any Personal Information through the Mobile Application and Services. We encourage parents and legal guardians to monitor their children\\'s Internet usage and to help enforce this Policy by instructing their children never to provide Personal Information through the Mobile Application and Services without their permission. If you have reason to believe that a child under the age of 18 has provided Personal Information to us through the Mobile Application and Services, please contact us. You must also be at least 16 years of age to consent to the processing of your Personal Information in your country (in some countries we may allow your parent or guardian to do so on your behalf).</p>\n" +
        "<h2>Links to other resources</h2>\n" +
        "<p>The Mobile Application and Services contain links to other resources that are not owned or controlled by us. Please be aware that we are not responsible for the privacy practices of such other resources or third parties. We encourage you to be aware when you leave the Mobile Application and Services and to read the privacy statements of each and every resource that may collect Personal Information.</p>\n" +
        "<h2>Information security</h2>\n" +
        "<p>We secure information you provide on computer servers in a controlled, secure environment, protected from unauthorized access, use, or disclosure. We maintain reasonable administrative, technical, and physical safeguards in an effort to protect against unauthorized access, use, modification, and disclosure of Personal Information in its control and custody. However, no data transmission over the Internet or wireless network can be guaranteed. Therefore, while we strive to protect your Personal Information, you acknowledge that (i) there are security and privacy limitations of the Internet which are beyond our control; (ii) the security, integrity, and privacy of any and all information and data exchanged between you and the Mobile Application and Services cannot be guaranteed; and (iii) any such information and data may be viewed or tampered with in transit by a third party, despite best efforts.</p>\n" +
        "<h2>Data breach</h2>\n" +
        "<p>In the event we become aware that the security of the Mobile Application and Services has been compromised or users Personal Information has been disclosed to unrelated third parties as a result of external activity, including, but not limited to, security attacks or fraud, we reserve the right to take reasonably appropriate measures, including, but not limited to, investigation and reporting, as well as notification to and cooperation with law enforcement authorities. In the event of a data breach, we will make reasonable efforts to notify affected individuals if we believe that there is a reasonable risk of harm to the user as a result of the breach or if notice is otherwise required by law. When we do, we will send you an email.</p>\n" +
        "<h2>Changes and amendments</h2>\n" +
        "<p>We reserve the right to modify this Policy or its terms relating to the Mobile Application and Services from time to time in our discretion and will notify you of any material changes to the way in which we treat Personal Information. When we do, we will revise the updated date at the bottom of this page. We may also provide notice to you in other ways in our discretion, such as through contact information you have provided. Any updated version of this Policy will be effective immediately upon the posting of the revised Policy unless otherwise specified. Your continued use of the Mobile Application and Services after the effective date of the revised Policy (or such other act specified at that time) will constitute your consent to those changes. However, we will not, without your consent, use your Personal Information in a manner materially different than what was stated at the time your Personal Information was collected. Policy was created with <a  href=\"https://www.websitepolicies.com/privacy-policy-generator\" target=\"_blank\">WebsitePolicies</a>.</p>\n" +
        "<h2>Acceptance of this policy</h2>\n" +
        "<p>You acknowledge that you have read this Policy and agree to all its terms and conditions. By accessing and using the Mobile Application and Services you agree to be bound by this Policy. If you do not agree to abide by the terms of this Policy, you are not authorized to access or use the Mobile Application and Services.</p>\n" +
        "<h2>Contacting us</h2>\n" +
        "<p>If you would like to contact us to understand more about this Policy or wish to contact us concerning any matter relating to individual rights and your Personal Information, you may send an email to had&#105;&#115;&#97;lh&#101;b&#64;&#103;m&#97;i&#108;&#46;c&#111;m</p>\n" +
        "<p>This document was last updated on October 24, 2020</p>"