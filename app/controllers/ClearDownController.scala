package controllers

import com.google.inject.{Inject, Singleton}
import repository.{BPMongoConnector, SubscriptionMongoConnector, TaxEnrolmentConnector}

/**
  * Created by david on 27/01/17.
  */

@Singleton
class ClearDownController @Inject()(
                                     subscriptionETMPMongoConnector: SubscriptionMongoConnector,
                                     subscriptionTaxEnrolmentConnector: TaxEnrolmentConnector,
                                     bPMongoConnector: BPMongoConnector
                                   ) {

  def clearDown(): Unit = {
    subscriptionETMPMongoConnector.repository.removeAll()
    subscriptionTaxEnrolmentConnector.issuerRepository.removeAll()
    subscriptionTaxEnrolmentConnector.subscriberRepository.removeAll()
    bPMongoConnector.repository.removeAll()
  }
}
