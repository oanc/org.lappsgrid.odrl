package org.lappsgrid.odrl

import com.github.jsonldjava.jena.JenaJSONLD
import odrlmodel.*
import org.apache.jena.riot.Lang
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 * @author Keith Suderman
 */
class Main {
    public static final String EXTENSION = ".odrl"

    Set<String> included = new HashSet<String>()
    File parentDir
    Binding bindings = new Binding()
    Lang lang

    public Main(Lang lang) {
        this.lang = lang
    }

    ClassLoader getLoader() {
        ClassLoader loader = Thread.currentThread().contextClassLoader;
        if (loader == null) {
            loader = this.class.classLoader
        }
        return loader
    }

    CompilerConfiguration getCompilerConfiguration() {
        ImportCustomizer customizer = new ImportCustomizer()
        def packages = [
                'odrlmodel'
//                'org.lappsgrid.api',
//                'org.lappsgrid.core',
//                'org.lappsgrid.client',
//                'org.lappsgrid.discriminator',
//                'org.lappsgrid.serialization',
//                'org.lappsgrid.serialization.lif',
//                'org.lappsgrid.serialization.datasource',
//                'org.lappsgrid.metadata',
//
//                'org.anc.lapps.pipeline',
//                'org.anc.io',
//                'org.anc.util',
//                'org.anc.xml'
        ]
        packages.each {
            customizer.addStarImports(it)
        }

        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(customizer)
        return configuration
    }

    void run(File file, args) {
        parentDir = file.parentFile
        run(file.text, args)
    }

    void run(String scriptString, args) {
        ClassLoader loader = getLoader()
        CompilerConfiguration configuration = getCompilerConfiguration()
        GroovyShell shell = new GroovyShell(loader, bindings, configuration)

        Script script = shell.parse(scriptString)
        if (args != null && args.size() > 0) {
            // Parse any command line arguements into a HashMap that will
            // be passed in to the user's script.
            def params = [:]
            args.each { arg ->
                String[] parts = arg.split('=')
                String name = parts[0].startsWith('-') ? parts[0][1..-1] : parts[0]
                String value = parts.size() > 1 ? parts[1] : Boolean.TRUE
                params[name] = value
            }
            script.binding.setVariable("args", params)
        }
        else {
            script.binding.setVariable("args", [:])
        }

        script.metaClass = getMetaClass(script.class, shell)
        try {
            script.run()
        }
        catch (Exception e) {
            println()
            println "Script execution threw an exception:"
            e.printStackTrace()
            println()
        }
    }

    MetaClass getMetaClass(Class<?> theClass, GroovyShell shell) {
        ExpandoMetaClass meta = new ExpandoMetaClass(theClass, false)
        meta.include = { String filename ->
            // Make sure we can find the file. The default behaviour is to
            // look in the same directory as the source script.
            // TODO Allow an absolute path to be specified.

            def filemaker
            if (parentDir != null) {
                filemaker = { String name ->
                    return new File(parentDir, name)
                }
            }
            else {
                filemaker = { String name ->
                    new File(name)
                }
            }

            File file = filemaker(filename)
            if (!file.exists() || file.isDirectory()) {
                file = filemaker(filename + EXTENSION)
                if (!file.exists()) {
                    throw new FileNotFoundException(filename)
                }
            }
            // Don't include the same file multiple times.
            if (included.contains(filename)) {
                return
            }
            included.add(filename)


            // Parse and run the script.
            Script included = shell.parse(file)
            included.metaClass = getMetaClass(included.class, shell)
            included.run()
        }

        meta.policy = { String uri, Closure cl ->
            PolicyDelegate delegate = new PolicyDelegate(uri)
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            println ODRLRDF.getRDF(delegate.policy, lang)
        }

        meta.set = { String uri, Closure cl ->
            PolicyDelegate delegate = new PolicyDelegate(uri)
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            delegate.policy.type = Policy.POLICY_SET
            println ODRLRDF.getRDF(delegate.policy, lang)
        }

        meta.request = { String uri, Closure cl ->
            PolicyDelegate delegate = new PolicyDelegate(uri)
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            delegate.policy.type = Policy.POLICY_REQUEST
            println ODRLRDF.getRDF(delegate.policy, lang)
        }

        meta.offer = { String uri, Closure cl ->
            PolicyDelegate delegate = new PolicyDelegate(uri)
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            delegate.policy.type = Policy.POLICY_OFFER
            println ODRLRDF.getRDF(delegate.policy, lang)
        }

        meta.agreement = { String uri, Closure cl ->
            PolicyDelegate delegate = new PolicyDelegate(uri)
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            delegate.policy.type = Policy.POLICY_AGREEMENT
            println ODRLRDF.getRDF(delegate.policy, lang)
        }

        meta.ticket = { String uri, Closure cl ->
            PolicyDelegate delegate = new PolicyDelegate(uri)
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            delegate.policy.type = Policy.POLICY_TICKET
            println ODRLRDF.getRDF(delegate.policy, lang)
        }

        meta.permission = { Closure cl ->
            PermissionsDelegate delegate = new PermissionsDelegate()
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            return delegate.permission
        }

        meta.prohibition = { Closure cl ->
            ProhibitionDelegate delegate = new ProhibitionDelegate()
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            return delegate.prohibition
        }

        meta.duty = { Closure cl ->
            DutyDelegate delegate = new DutyDelegate()
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            return delegate.duty
        }

        meta.constraint = { Closure cl ->
            ConstraintDelegate delegate = new ConstraintDelegate()
            cl.delegate = delegate
            cl.resolveStrategy = Closure.DELEGATE_FIRST
            cl()
            return delegate.constraint
        }

        meta.action = { String uri ->
            return new Action(uri);
        }

        meta.party = { String uri ->
            return new Party(uri)
        }

//        meta.Datasource = { Closure cl ->
//            cl.delegate = new DataSourceDelegate()
//            cl.resolveStrategy = Closure.DELEGATE_FIRST
//            cl()
//            String url = cl.delegate.getServiceUrl()
//            String user = cl.delegate.server.username
//            String pass = cl.delegate.server.password
//            return new DataSourceClient(url, user, pass)
//        }
//
//        meta.Server = { Closure cl ->
//            cl.delegate = new ServerDelegate()
//            cl.resolveStrategy = Closure.DELEGATE_FIRST
//            cl()
//            return new Server(cl.delegate)
//        }
//
//        meta.Service = { Closure cl ->
//            cl.delegate = new ServiceDelegate()
//            cl.resolveStrategy = Closure.DELEGATE_FIRST
//            cl()
//            def service = new Service(cl.delegate)
//            def url = service.getServiceUrl();
//            def user = service.server.username
//            def pass = service.server.password
//            return new ServiceClient(url, user, pass)
//        }
//
//        meta.Pipeline = { Closure cl ->
//            cl.delegate = new PipelineDelegate()
//            cl.resolveStrategy = Closure.DELEGATE_FIRST
//            cl()
//            return cl.delegate
//        }

        meta.initialize()
        return meta
    }

    Map createPolicies() {
        Map policies = [
            set:{ String uri, Closure cl ->
                PolicyDelegate delegate = new PolicyDelegate(uri)
                cl.delegate = delegate
                cl.resolveStrategy = Closure.DELEGATE_FIRST
                cl()
                delegate.policy.type = Policy.POLICY_SET
                println ODRLRDF.getRDF(delegate.policy, lang)
            },
            offer:{ String uri, Closure cl ->
                PolicyDelegate delegate = new PolicyDelegate(uri)
                cl.delegate = delegate
                cl.resolveStrategy = Closure.DELEGATE_FIRST
                cl()
                delegate.policy.type = Policy.POLICY_OFFER
                println ODRLRDF.getRDF(delegate.policy, lang)
            }
        ]
        return policies
    }

    static void main(args) {
        CliBuilder cli = new CliBuilder()
        cli.usage = "odrl [-h|-v] [-l (rdf|jsonld|ttl|n3)] <file>"
        cli.header = "Generates RDF ODRL policies from the input DSL file."
        cli.h(longOpt:'help', 'displays this help message.')
        cli.v(longOpt:'version', 'displays the applcation version number')
        cli.l(longOpt:'lang', args:1, 'The specified output language')
        cli.footer = "The supported output langages are rdf, jsonld, n3, and ttl. Default is rdf."

        def params = cli.parse(args)
        if (!params) {
            return
        }

        if (params.h) {
            cli.usage()
            return
        }

        if (params.v) {
            println()
            println "LAPPS Open Digital Rights Language (ODRL) v${Version.version}"
            println "Copyright 2015 American National Corpus"
            println()
            return
        }

        Lang lang = Lang.RDFXML
        if (params.l) {
            if (params.l == 'rdf') {
                lang = Lang.RDFXML
            }
            else if (params.l == 'jsonld') {
                JenaJSONLD.init();
                lang = Lang.JSONLD
            }
            else if (params.l == 'ttl') {
                lang = Lang.TTL
            }
            else if (params.l == 'n3') {
                lang = Lang.N3
            }
            else {
                println "ERROR: Unsupported output language ${params.l}"
                cli.usage()
                return
            }
        }
        List<String> arguments = params.arguments()
        if (arguments == null || arguments.size() == 0) {
            println()
            println "ERROR: no filename provided."
            println()
            cli.usage()
            return
        }

        def argv = null
        if (arguments.size() > 1) {
            argv = arguments[1..-1]
        }
        Main app = new Main(lang)
        File file = new File(arguments[0])
        new Main(lang).run(new File(arguments[0]), argv)
    }
}
