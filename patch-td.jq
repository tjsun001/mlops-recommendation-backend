del(
  .taskDefinitionArn,.revision,.status,.requiresAttributes,.compatibilities,
  .registeredAt,.registeredBy,.deregisteredAt,.inferenceAccelerators,.ephemeralStorage
)
| (.containerDefinitions[] | select(.name=="product-service") | .environment) |= (
    ( . // [] )
    | map(select(.name!="SPRING_KAFKA_BOOTSTRAP_SERVERS"))
    + [{"name":"SPRING_KAFKA_BOOTSTRAP_SERVERS","value":$BOOT}]
  )
