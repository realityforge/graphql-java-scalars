require 'buildr/git_auto_version'
require 'buildr/gpg'

desc 'graphql-java scalar implementations'
define 'graphql-java-scalars' do
  project.group = 'org.realityforge.graphql.scalars'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'
  compile.options.warnings = true
  compile.options.other = %w(-Werror -Xmaxerrs 10000 -Xmaxwarns 10000)

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/graphql-java-scalars')
  pom.add_developer('realityforge', 'Peter Donald')

  compile.with :javax_annotation,
               :graphql_java

  test.using :testng

  package(:jar)
  package(:sources)
  package(:javadoc)

  ipr.add_component_from_artifact(:idea_codestyle)
  ipr.add_code_insight_settings
  ipr.add_nullable_manager
  ipr.add_javac_settings('-Xlint:all -Werror -Xmaxerrs 10000 -Xmaxwarns 10000')
end
