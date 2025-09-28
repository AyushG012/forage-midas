package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import java.util.Optional;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class KafkaListener {

    private final UserRepository userRepo;
    private final TransactionRepository transactionRepo;
    private final RestTemplate restTemplate;

    @Value("${general.kafka-topic}")
    private String topic;

    @Value("${general.incentive-api-url}")
    private String incentiveApiUrl;

    public KafkaListener(UserRepository userRepo, TransactionRepository transactionRepo) {
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.restTemplate = new RestTemplate();
    }

    @org.springframework.kafka.annotation.KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    @Transactional
    public void listen(Transaction transaction) {
        Optional<UserRecord> senderOpt = userRepo.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepo.findById(transaction.getRecipientId());

        System.out.println("Received transaction: " + transaction);

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();


            if (sender.getBalance() >= transaction.getAmount()) {
                // Call Incentives API
                Incentive incentive = restTemplate.postForObject(
                        incentiveApiUrl,
                        transaction,
                        Incentive.class
                );
                System.out.println(incentive);
                float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;


                // Update balances
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);


                userRepo.save(sender);
                userRepo.save(recipient);
                transactionRepo.save(new TransactionEntity(sender, recipient, transaction.getAmount(), incentiveAmount));
            }
            // else discard
        }
// else discard if either user is missing
    }
}