package life.league.healthjourney.journey.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import life.league.core.util.ApplyModuleAdapters
import life.league.healthjourney.journey.models.CompletionMethod
import life.league.healthjourney.journey.models.CompletionMethodType


object ApplyHealthJourneyAdapters: ApplyModuleAdapters() {
    override fun invoke(builder: Moshi.Builder): Moshi.Builder = builder.run {
        add(PolymorphicJsonAdapterFactory.of(CompletionMethod::class.java, "type")
            .withSubtype(CompletionMethod.Base::class.java, CompletionMethodType.base.name)
            .withSubtype(CompletionMethod.MultiStep::class.java, CompletionMethodType.step.name)
            .withDefaultValue(CompletionMethod.Unsupported))
    }
}
