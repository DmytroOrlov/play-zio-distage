import commons.AppEnv
import controllers.{AssetsComponents, UserController}
import play.api.ApplicationLoader.Context
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import router.Routes
import zio._

class AppLoader extends ApplicationLoader {

  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
    new modules.AppComponentsInstances(context).application
  }
}

package object modules {

  class AppComponentsInstances(context: Context) extends BuiltInComponentsFromContext(context) with AssetsComponents {
    import zio.interop.catz._

    implicit val rts = Runtime.default

    type Eff[A] = IO[Throwable, A]

    private implicit val (appContext: Layer[Throwable, AppEnv], release: Eff[Unit]) =
      rts.unsafeRun(AppEnv.live.memoize.toResource[Eff].allocated)

    applicationLifecycle.addStopHook(() => rts.unsafeRunToFuture(release))

    override def router: Router = new Routes(httpErrorHandler, new UserController(controllerComponents), assets)

    override def httpFilters: Seq[EssentialFilter] = Seq()

  }

}
