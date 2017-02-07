package controller

import com.google.inject.Injector
import module.BindModule

/**
  * DIでインスタンスを注入する際のtrait.
  */
trait DiInjector {
  /** Injector. */
  val injector:Injector = BindModule.injector
}
