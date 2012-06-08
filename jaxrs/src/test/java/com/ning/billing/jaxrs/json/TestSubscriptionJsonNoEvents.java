/*
 * Copyright 2010-2012 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.jaxrs.json;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.ning.billing.catalog.api.BillingPeriod;
import com.ning.billing.catalog.api.InternationalPrice;
import com.ning.billing.catalog.api.Plan;
import com.ning.billing.catalog.api.PlanPhase;
import com.ning.billing.catalog.api.PriceList;
import com.ning.billing.catalog.api.Product;
import com.ning.billing.catalog.api.ProductCategory;
import com.ning.billing.entitlement.api.user.Subscription;

public class TestSubscriptionJsonNoEvents {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test(groups = "fast")
    public void testJson() throws Exception {
        final String subscriptionId = UUID.randomUUID().toString();
        final String bundleId = UUID.randomUUID().toString();
        final DateTime startDate = new DateTime(DateTimeZone.UTC);
        final String productName = UUID.randomUUID().toString();
        final String productCategory = UUID.randomUUID().toString();
        final String billingPeriod = UUID.randomUUID().toString();
        final String priceList = UUID.randomUUID().toString();
        final DateTime chargedThroughDate = new DateTime(DateTimeZone.UTC);
        final SubscriptionJsonNoEvents subscriptionJsonNoEvents = new SubscriptionJsonNoEvents(subscriptionId, bundleId, startDate,
                                                                                               productName, productCategory, billingPeriod,
                                                                                               priceList, chargedThroughDate);
        Assert.assertEquals(subscriptionJsonNoEvents.getSubscriptionId(), subscriptionId);
        Assert.assertEquals(subscriptionJsonNoEvents.getBundleId(), bundleId);
        Assert.assertEquals(subscriptionJsonNoEvents.getStartDate(), startDate);
        Assert.assertEquals(subscriptionJsonNoEvents.getProductName(), productName);
        Assert.assertEquals(subscriptionJsonNoEvents.getProductCategory(), productCategory);
        Assert.assertEquals(subscriptionJsonNoEvents.getBillingPeriod(), billingPeriod);
        Assert.assertEquals(subscriptionJsonNoEvents.getPriceList(), priceList);
        Assert.assertEquals(subscriptionJsonNoEvents.getChargedThroughDate(), chargedThroughDate);

        final String asJson = mapper.writeValueAsString(subscriptionJsonNoEvents);
        Assert.assertEquals(asJson, "{\"subscriptionId\":\"" + subscriptionJsonNoEvents.getSubscriptionId() + "\"," +
                "\"bundleId\":\"" + subscriptionJsonNoEvents.getBundleId() + "\"," +
                "\"startDate\":\"" + subscriptionJsonNoEvents.getStartDate().toDateTimeISO().toString() + "\"," +
                "\"productName\":\"" + subscriptionJsonNoEvents.getProductName() + "\"," +
                "\"productCategory\":\"" + subscriptionJsonNoEvents.getProductCategory() + "\"," +
                "\"billingPeriod\":\"" + subscriptionJsonNoEvents.getBillingPeriod() + "\"," +
                "\"priceList\":\"" + subscriptionJsonNoEvents.getPriceList() + "\"," +
                "\"chargedThroughDate\":\"" + subscriptionJsonNoEvents.getChargedThroughDate().toDateTimeISO().toString() + "\"}");

        final SubscriptionJsonNoEvents fromJson = mapper.readValue(asJson, SubscriptionJsonNoEvents.class);
        Assert.assertEquals(fromJson, subscriptionJsonNoEvents);
    }

    @Test(groups = "fast")
    public void testFromSubscriptionSubscription() throws Exception {
        final Product product = Mockito.mock(Product.class);
        Mockito.when(product.getName()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(product.getCategory()).thenReturn(ProductCategory.STANDALONE);

        final InternationalPrice price = Mockito.mock(InternationalPrice.class);
        final PlanPhase planPhase = Mockito.mock(PlanPhase.class);
        Mockito.when(planPhase.getRecurringPrice()).thenReturn(price);

        final Plan plan = Mockito.mock(Plan.class);
        Mockito.when(plan.getProduct()).thenReturn(product);
        Mockito.when(plan.getName()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(plan.getBillingPeriod()).thenReturn(BillingPeriod.QUARTERLY);
        Mockito.when(plan.getFinalPhase()).thenReturn(planPhase);

        final PriceList priceList = Mockito.mock(PriceList.class);

        final Subscription subscription = Mockito.mock(Subscription.class);
        Mockito.when(subscription.getId()).thenReturn(UUID.randomUUID());
        Mockito.when(subscription.getBundleId()).thenReturn(UUID.randomUUID());
        Mockito.when(subscription.getStartDate()).thenReturn(new DateTime(DateTimeZone.UTC));
        Mockito.when(subscription.getCurrentPlan()).thenReturn(plan);
        Mockito.when(subscription.getCurrentPriceList()).thenReturn(priceList);
        Mockito.when(subscription.getChargedThroughDate()).thenReturn(new DateTime(DateTimeZone.UTC));

        final SubscriptionJsonNoEvents subscriptionJsonNoEvents = new SubscriptionJsonNoEvents(subscription);
        Assert.assertEquals(subscriptionJsonNoEvents.getSubscriptionId(), subscription.getId().toString());
        Assert.assertEquals(subscriptionJsonNoEvents.getStartDate(), subscription.getStartDate());
        Assert.assertEquals(subscriptionJsonNoEvents.getBundleId(), subscription.getBundleId().toString());
        Assert.assertEquals(subscriptionJsonNoEvents.getProductName(), subscription.getCurrentPlan().getProduct().getName());
        Assert.assertEquals(subscriptionJsonNoEvents.getProductCategory(), subscription.getCurrentPlan().getProduct().getCategory().toString());
        Assert.assertEquals(subscriptionJsonNoEvents.getBillingPeriod(), subscription.getCurrentPlan().getBillingPeriod().toString());
        Assert.assertEquals(subscriptionJsonNoEvents.getChargedThroughDate(), subscription.getChargedThroughDate());
    }
}
